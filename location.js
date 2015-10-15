var Location = function() {
  var _this = this;
  this.longitude = 0;
  this.latitude = 0;
  this.accuracy = -1;
  
  this.setPosition = function(pos) {
    _this.latitude = pos.coords.latitude;
    _this.longitude = pos.coords.longitude;
    _this.accuracy = pos.coords.accuracy;
  }
  
  this.reqPosition = function() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(this.setPosition);
    } else {
      // No geolocation, we dun goofed!
    }
  }
  
  this.hasPosition = function() {
    return ~accuracy;
  }
  
  // setup:
  this.reqPosition();
}

var map;
var location = new Location();
function initMap() {
  map = new google.maps.Map(document.querySelector("#map"), {
    center: {lat: location.latitude, lng: location.longitude},
    zoom: 8
  });
}