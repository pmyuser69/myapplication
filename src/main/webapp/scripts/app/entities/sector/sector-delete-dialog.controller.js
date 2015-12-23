'use strict';

angular.module('myappApp')
	.controller('SectorDeleteController', function($scope, $uibModalInstance, entity, Sector) {

        $scope.sector = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Sector.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
