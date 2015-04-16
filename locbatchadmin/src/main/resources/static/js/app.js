/**
 * Main application level file
 */
'use strict';

var locobatchApp = angular.module('locobatchApp', [ 'ngRoute',
		'locoControllers' ]);


locobatchApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.
	when('/Dashboard', {
		templateUrl: '/partials/dashboardSnippet.html',
		controller: 'DashboardCtrl'
	}).
	when('/Jobs', {
		templateUrl: '/partials/jobs.html'
	}).
	otherwise({
		redirectTo: '/Dashboard'
	});
} ]);