'use strict';
define(['frontend', 'services/RestFulResponseFactory'], function(frontend) {

  frontend.factory('LoggedUserFactory', function(RestFulResponse, $window, $q) {
    var loggedUser = {
      logged: false,
      expDate: undefined
    };
    var mutex = {
      value: false
    }

    var LoggedUserFactory = {
      getLoggedUser: function () {
        return loggedUser;
      },
      saveToken: function (token) {
        return $q(function (resolve, reject) {
          mutex.value = true;
          loggedUser.expDate = RestFulResponse.setToken(token);
          RestFulResponse.withAuth(loggedUser).then(function (r) {
            r.one("user").get().then(function (user) {
              Object.assign(loggedUser, user.data ? user.data : user);
              loggedUser.logged = true;
              mutex.value = false;
              resolve(loggedUser);
            }).catch(function (err) {
              mutex.value = false;
              reject(err);
            });
          }).catch(function (err) {
            mutex.value = false;
            reject(err);
          });

        })
      },
      login: function (user) {
        return $q(function (resolve, reject) {
          mutex.value = true;
          RestFulResponse.noAuth().all("user").post(user).then(function (resp) {
            LoggedUserFactory.saveToken(resp.headers("authorization")).then(function (r) {
              resolve(r);
            }).catch(function (err) {
              reject(err);
            });
          }).catch(function (err) {
            mutex.value = false;
            reject(err);
          });
        });
      },

      logout: function () {
        return $q(function (resolve, reject) {
          RestFulResponse.noAuth().one("/user/refresh_token").remove().then(function () {
            var aux = {
              logged: false,
              expDate: undefined
            };
            Object.assign(loggedUser, aux);
            RestFulResponse.clearHeaders();
            resolve();
          }).catch(function (err) {
            reject(err)
          })
        });
      },

      isLogged: function () {
        return $q(function (resolve) {

          var f = function () {
            if (!mutex.value) {
              return resolve(loggedUser.logged);
            }
            setTimeout(f, 50);
          };

          f();

        });
      },

    //  esto es solo debería usarse en index controller
      startLoggedUserCheck: function () {
        mutex.value = true;
      },

      finishLoggedUserCheck: function () {
        mutex.value = false;
      }
    };

    return LoggedUserFactory;
  });
});
