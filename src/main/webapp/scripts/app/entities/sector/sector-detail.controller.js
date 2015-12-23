'use strict';

angular.module('myappApp')
    .controller('SectorDetailController', function ($scope, $rootScope, $stateParams, entity, Sector) {
        $scope.sector = entity;
        $scope.load = function (id) {
            Sector.get({id: id}, function(result) {
                $scope.sector = result;
            });
        };
        var unsubscribe = $rootScope.$on('myappApp:sectorUpdate', function(event, result) {
            $scope.sector = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
