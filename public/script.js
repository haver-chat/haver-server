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
    return ~_this.accuracy;
  }
  
  // setup:
  this.reqPosition();
}

var Socket = function() {
  var _this = this;
  this.socket = null;
  this.types = {
    LOCATION: 0,
    MESSAGE: 1,
    ROOM_INFO: 2
  };
  this.send = null;
  
  this.connect = function() {
    var protocol = location.protocol.split('http').join('ws') + '//';
    //_this.socket = new WebSocket(protocol + location.host + ':8080');
    _this.socket = new WebSocket('ws://127.0.0.1:8080');
    
    _this.socket.onopen = function() {
      var pos = null;
      pos = new Position();
      _this.send(_this.types.LOCATION, pos);
    }
    
    _this.socket.onclose = function() {
      _this.reconnect();
    }
    
    _this.socket.onmessage = function(message) {
      console.log(message);
      var cum = JSON.parse(message.data);
      console.log(cum);
      if (cum.type == _this.types.ROOM_INFO) {
        var whatever = {
          name: "Bingo!",
          radius: 10
        }
        _this.send(_this.types.ROOM_INFO, whatever);
      }
      
    }
    
    _this.send = function(type, data) {
      data['type'] = type;
      console.log(JSON.stringify(data)); // log what we send
      _this.socket.send(JSON.stringify(data));
    }
    
  }
  
  this.reconnect = function() {
    
  }
}


var soc = new Socket();
soc.connect();