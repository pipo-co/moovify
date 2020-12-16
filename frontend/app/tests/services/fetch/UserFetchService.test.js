define(['angular', 'angularMocks', 'frontend', 'services/fetch/UserFetchService', 'restangular', 'polyfillURLSearchParams'], function(angular) {

  describe('UserFetchService', function() {

    var $scope;
    var $q;
    var $httpBackend;
    var Restangular;
    var $provide;

    beforeEach(angular.mock.module('frontend'));

    beforeEach(module(function (_$provide_) {
      $provide = _$provide_;
    }));

    beforeEach(inject(function (_$rootScope_, _$q_, _$httpBackend_, _Restangular_) {
      $scope = _$rootScope_;
      $q = _$q_;
      $httpBackend = _$httpBackend_;
      Restangular = _Restangular_;
    }));

    beforeEach(function() {
      var ReqFullResponse = Restangular.withConfig(function (RestangularConfigurer) {
        RestangularConfigurer.setFullResponse(true);
      });

      $provide.value('RestFulResponse', {withAuthIfPossible: function() {return $q.resolve(ReqFullResponse)}});

      $provide.value('LoggedUserFactory', {
        getLoggedUser: function() { return null; }
      });

      $provide.value('LinkParserService', {parse: function() {return 10}});
    });

    it('search users success test', inject(function (UserFetchService) {

      var query = "queryParam";
      var role = "roleParam";
      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var users = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/users\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('query')).toEqual(query);
        expect(searchParams.get('role')).toEqual(role);
        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      UserFetchService.searchUsers(query, role, enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(users);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {query: query, role: role, enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));

    it('fetch users success test', inject(function (UserFetchService) {

      var enabled = true;
      var orderBy = "orderByParam";
      var pageSize = 10;
      var pageNumber = 15;

      var users = [{id: 1}, {id: 2}, {id: 3}, {id: 4}];

      $httpBackend.expectGET(/.*\/api\/users\?.*/).respond(function(method, url) {

        var searchParams = new URLSearchParams(url.substring(url.indexOf('?'), url.length));

        expect(searchParams.get('enabled')).toEqual(enabled.toString());
        expect(searchParams.get('orderBy')).toEqual(orderBy);
        expect(searchParams.get('pageSize')).toEqual(pageSize.toString());
        expect(searchParams.get('pageNumber')).toEqual(pageNumber.toString());

        return [200, [{id: 1}, {id: 2}, {id: 3}, {id: 4}]];
      });

      UserFetchService.fetchUsers('/users', enabled, orderBy, pageSize, pageNumber).then(function (response) {
        expect(response.collection.map(function(u) {return u.originalElement })).toEqual(users);
        expect(response.paginationParams).toEqual({pageSize: pageSize, currentPage: pageNumber, lastPage: 0});
        expect(response.queryParams).toEqual(
          {enabled: enabled, orderBy: orderBy, pageSize: pageSize, pageNumber: pageNumber })
      });

      $httpBackend.flush();

      $scope.$digest();
    }));
  });
});
