var locoControllers = angular.module('locoControllers', []);

locoControllers.controller('offerCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/api/offerCount').success(function(data) {
				$scope.offerCnt = data;
				console.log("I am in console");
			})
			
		} ]);