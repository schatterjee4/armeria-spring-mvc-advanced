'use strict';

$(function () {
  $.getJSON('/api/hello', function (data) {
    $('#name').text(data.name);
  });
});
