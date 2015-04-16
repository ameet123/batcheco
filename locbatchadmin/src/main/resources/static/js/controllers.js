var locoControllers = angular.module('locoControllers', []);

locoControllers.controller('offerCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/api/offerCount').success(function(data) {
				$scope.offerCnt = data;
				console.log("I am in console");
			})
		} ]);

locoControllers.controller('criteriaCntCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/api/criteriaSqlCount').success(function(data) {
				$scope.criteriaCnt = data;
				console.log("running criteria count");
			})
		} ]);

locoControllers.controller('jobExecCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/api/jobCount').success(function(data) {
				$scope.jobCnt = data;
				console.log("running job count");
			})
		} ]);


/**
 * singular controller for all dashboard queries
 */
locoControllers.controller('DashboardCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/api/offerCount').success(function(data) {
				$scope.offerCnt = data;
				console.log("offer count:" + offerCnt);
			})
			// job count
			$http.get('/api/jobCount').success(function(data) {
				$scope.jobCnt = data;
				console.log("running job count");
			});
			// criteria sql count
			$http.get('/api/criteriaSqlCount').success(function(data) {
				$scope.criteriaCnt = data;
				console.log("running criteria count");
			});
		} ]);