'use strict';

angular.module('myappApp')
    .factory('SectorSearch', function ($resource) {
        return $resource('api/_search/sectors/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
