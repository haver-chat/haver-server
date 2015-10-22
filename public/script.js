var sendPos = function() {
  var send = function(pos) {
    _this.send(_this.types.LOCATION, pos);
  }
  var pos = new Position(send);
}


var Position = function(callback) {
  var _this = this;
  this.longitude = 0;
  this.latitude = 0;
  this.accuracy = -1;
  this.callback = callback;
  
  this.setPosition = function(pos) {
    _this.latitude = pos.coords.latitude;
    _this.longitude = pos.coords.longitude;
    _this.accuracy = pos.coords.accuracy;
    if (typeof callback != 'undefined') {
      callback(_this);
    }
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

var Post = function(content, recipients) {
  this.content = content;
  this.to = typeof recipients != "undefined" ? recipients : [];
}

var RoomInfo = function(name, radius) {
  this.name = name;
  this.radius = radius;
}

var Socket = function() {
  var _this = this;
  this.socket = null;
  this.types = {
    LOCATION: 0,
    POST: 1,
    ROOM_INFO: 2
  };
  this.send = null;
  
  this.connect = function() {
    var protocol = location.protocol.split('http').join('ws') + '//';
    var host = (protocol == "file://") ? 'ws://127.0.0.1:8080' : protocol + location.host + ':8080';
    _this.socket = new WebSocket(host);
    
    _this.socket.onopen = function() {
      sendPos();
    }
    
    _this.socket.onclose = function() {
      console.log("Disconnected");
      _this.reconnect();
    }
    
    _this.socket.onmessage = function(res) {
      var message = JSON.parse(res.data);
      console.log(message);
      var types = _this.types;
      switch(message.type) {
        case types.ROOM_INFO:
          var room = new RoomInfo("Bingo!", 100);
          _this.send(_this.types.ROOM_INFO, room);
          break;
        case types.LOCATION:
          sendPos();
          break;
        case types.POST:
          addMessage(message);
          break;
        default:
          break;
      }
    }
    
    _this.send = function(type, data) {
      data['type'] = type;
      console.log(JSON.stringify(data)); // log what we send
      _this.socket.send(JSON.stringify(data));
    }
    
    var sendPos = function() {
      var send = function(pos) {
        console.log("Sending position: " + pos);
        _this.send(_this.types.LOCATION, pos);
      }
      var pos = new Position(send);
    }
    
    var addMessage = function(message) {
      console.log("Adding messsage to UL");
      var li = document.createElement('li');
      li.innerHTML += message.from + ": " + message.content;
      document.querySelector('#chat ul').appendChild(li);
    } 
    
  }
  
  this.reconnect = function() {
    
  }
}

document.querySelector("form#chat-form").onsubmit = function() {
  soc.send(soc.types.POST, new Post(document.querySelector("input[name=msg-box]").value));
  document.querySelector("input[name=msg-box]").value = "";
  return false;
}

var soc = new Socket();
soc.connect();