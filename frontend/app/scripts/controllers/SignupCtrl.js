define(['frontend', 'services/LoginService', 'services/PageTitleService', 'services/RestFulResponseFactory'], function(frontend) {

    'use strict';
    frontend.controller('SignupCtrl', function($scope, LoggedUserFactory, PageTitle, RestFulResponse, $location, $translate, $document) {
      PageTitle.setTitle('asd') //TODO: cambiar la key

      if(LoggedUserFactory.getLoggedUser().logged) {
        $location.path('/');
      }

      $scope.signUpBtnPressed = false;
      $scope.signUpError = false;

      $scope.user = {};

      $scope.nameConstrains = {
        pattern: /^[a-zA-Z ]*$/,
        minLen: 2,
        maxLen: 50
      }

      $scope.usernameConstrains = {
        pattern: /^[a-zA-Z0-9#_]+$/,
        minLen: 6,
        maxLen: 50
      }

      $scope.passwordConstrains = {
        pattern: /^[^\s]+$/,
        minLen: 12,
        maxLen: 30
      }

      var fieldErrors = {
        name: {
          i18nKey: 'USER_CREATE_NAME',
          message: ''
        },
        email: {
          i18nKey: 'USER_CREATE_EMAIL',
          message: ''
        },
        username: {
          i18nKey: 'USER_CREATE_USERNAME',
          message: ''
        },
        password: {
          i18nKey: 'USER_CREATE_PASSWORD',
          message: ''
        },
      }

      $scope.avatar = {
        file: undefined,
        error: false
      }

      var inputFile = angular.element(document.getElementById('avatar'))[0];

      inputFile.addEventListener('change', function () {
        $scope.avatar.file = inputFile.files[0];
        if($scope.avatar.file !== undefined && $scope.avatar.file.size > 1000000) {
          $scope.avatar.file = undefined;
          $scope.avatar.error = true;
        } else {
          $scope.avatar.error = false;
        }
        $scope.$apply();
      });

      $scope.signup = function (user) {

        $scope.signUpBtnPressed = true;

        if(
          !$scope.fieldIsNotValid('name') &&
          !$scope.emailIsNotValid() &&
          !$scope.fieldIsNotValid('username') &&
          !$scope.fieldIsNotValid('password') &&
          !$scope.fieldIsNotValid('repeatPassword') &&
          !$scope.passwordsNotEquals()
        )
        {
          RestFulResponse.noAuth().all('users').post(user).then(function() {
            var aux_user = {
              username: user.username,
              password: user.password
            }
            LoggedUserFactory.login(aux_user).then(function (loggedUser) {
              if($scope.avatar.file !== undefined) {
                var fd = new FormData();
                fd.append('avatar', $scope.avatar.file);
                RestFulResponse.withAuth(loggedUser).then(function (r) {
                  r.one('/user/avatar').customPUT(fd, undefined, undefined, {'Content-Type': undefined}).then(function (resp) {
                    // esto es para que angular detecte que hubo un cambio y pida de nuevo la imagen
                    $scope.loggedUser.avatar = resp.headers('Content-Location');
                    $scope.$apply();
                  });
                });
              }
              $location.path('/') // TODO: poner la ruta del perfil
            })
          }).catch(function(err) {
            err.data.forEach(function(e){
                $translate(fieldErrors[e['attribute']].i18nKey).then(function(field) {
                  $translate('FORM_DUPLICATED_FIELD_ERROR', {field: field}).then(function(msg) {
                    fieldErrors[e['attribute']].message = msg;
                  }).catch(function(err) { console.log('inside', err) });
                }).catch(function(err){ console.log('outside', err) });
              });
          });
        }
      }

      $scope.fieldRequired = function (field) {
        return $scope.signUpBtnPressed && $scope.signupForm[field].$error.required !== undefined;
      }

      // fieldRequired || cons1 || cons2 || ... || consN
      $scope.fieldIsNotValid = function (field) {
        return $scope.fieldRequired(field) || $scope.signupForm[field].$error.pattern || $scope.signupForm[field].$error.minlength || $scope.signupForm[field].$error.maxlength;
      }

      $scope.emailIsNotValid = function () {
        return $scope.fieldRequired("email") || $scope.signupForm.email.$error.email;
      }

      $scope.passwordsNotEquals = function () {
        return $scope.user.password !== $scope.user.repeatPassword && !($scope.user.password === undefined || $scope.user.repeatPassword === undefined);
      }

      $scope.checkFieldError = function (field) {
        if(fieldErrors[field] === undefined) {
          throw 'Field ' + field + 'does not exists!';
        }

        return fieldErrors[field].message !== '';
      }

      $scope.getFieldErrors = function (field) {
        return fieldErrors[field].message;
      }

      $scope.resetFieldErrors = function (field) {
        fieldErrors[field].message = '';
      }

    });

});
