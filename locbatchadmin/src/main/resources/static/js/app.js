/**
 * Main application level file
 */
'use strict';

var locobatchApp = angular.module('locobatchApp', [ 'ngRoute', 'smart-table','ui.bootstrap',
		'locoControllers' ]);


locobatchApp.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.
	when('/BatchFlow', {
		templateUrl: '/partials/batchflow.html'
	}).
	when('/Dashboard', {
		templateUrl: '/partials/dashboardSnippet.html',
		controller: 'DashboardCtrl'
	}).
	when('/Jobs', {
		templateUrl: '/partials/jobs.html',
		controller: 'allJobsCtrl'
	}).	
	otherwise({
		redirectTo: '/BatchFlow'
	});
} ]);