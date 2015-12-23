'use strict';

angular.module('myappApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sector', {
                parent: 'entity',
                url: '/sectors',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'myappApp.sector.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sector/sectors.html',
                        controller: 'SectorController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sector');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('sector.detail', {
                parent: 'entity',
                url: '/sector/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'myappApp.sector.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/sector/sector-detail.html',
                        controller: 'SectorDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sector');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Sector', function($stateParams, Sector) {
                        return Sector.get({id : $stateParams.id});
                    }]
                }
            })
            .state('sector.new', {
                parent: 'sector',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/sector/sector-dialog.html',
                        controller: 'SectorDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    description: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('sector', null, { reload: true });
                    }, function() {
                        $state.go('sector');
                    })
                }]
            })
            .state('sector.edit', {
                parent: 'sector',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/sector/sector-dialog.html',
                        controller: 'SectorDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Sector', function(Sector) {
                                return Sector.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('sector', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('sector.delete', {
                parent: 'sector',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/sector/sector-delete-dialog.html',
                        controller: 'SectorDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Sector', function(Sector) {
                                return Sector.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('sector', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
