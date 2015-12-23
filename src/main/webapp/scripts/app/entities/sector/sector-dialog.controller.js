'use strict';

angular.module('myappApp').controller('SectorDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sector',
        function($scope, $stateParams, $uibModalInstance, entity, Sector) {

        $scope.sector = entity;
        $scope.load = function(id) {
            Sector.get({id : id}, function(result) {
                $scope.sector = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('myappApp:sectorUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.sector.id != null) {
                Sector.update($scope.sector, onSaveSuccess, onSaveError);
            } else {
                Sector.save($scope.sector, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
