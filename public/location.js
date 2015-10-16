var Position = function() {
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

/*var map;
var pos = new Position();
function initMap() {
  var coords = {lat: pos.latitude, lng: pos.longitude}
  map = new google.maps.Map(document.querySelector("#map"), {
    center: coords,
    zoom: 16  
  });
  var marker = new google.maps.Marker({
    position: coords,
    map: map,
    title: 'Hello World!'
  });
  var accuracyRadius = new google.maps.Circle({
    strokeColor: '#FF0000',
    strokeOpacity: 0.5,
    strokeWeight: 2,
    map: map,
    center: coords,
    radius: pos.accuracy
  });
  document.querySelector("#map").style.height = window.innerHeight + "px";
  console.log("Latitude: " + pos.latitude);
  console.log("Longitude: " + pos.longitude);
}


function getCurrentPosition(callback) {
  // find data
  var data = { 'whatever': 0 };
  callback(data);
}

var whatever = function(pos) {
  console.log(JSON.stringify(pos));
}*/

getCurrentPosition(whatever);