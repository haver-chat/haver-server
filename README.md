![Does it build](https://api.travis-ci.org/haver-chat/haver-server.svg?branch=master)

# Haver Server

### What is it?

[Haver](http://haver.chat) is a local chat program. The server creates a group
for users in a specified radius, so that users in a small area can talk to each
other.

### But why would you want to talk to people in the same room?

Haver is anonymous, which means that nobody in the room knows who you are.

When you join a room you are given a Monopoly piece, but you never sign up or
choose a username. Because of this, you can identify people's messages as being
posted by that person, but not who that person is.

We think that this will be used mostly by students in lectures and classes, and
by people at conventions, but it can be used anywhere that people are: theme
parks, shop queues, coffee shops, etc.

### How's it work?

Haver is split into a server and client model. We considered using a form of
communication like bluetooth or local Wi-Fi, but these technologies limit the
size of the network to the range of this signal. Since our server creates a
virtual room, the size of the room can be anything we want!

The Haver server (this repo) is written in Java, and the client is currently a
[website](http://haver.chat), but soon we will start working on mobile clients
to offer a more seemless experience for mobile users.

The client-server interaction uses WebSockets, which means that communications
are near instant, and we are using the HTML5 Geolocation API to keep the
current [client implementation](https://github.com/haver-chat/haver-web-client)
simple.

## Sounds cool, let me work on it!

### How to build?

We're using gradle, import the project into your favourite IDE, use
`gradle build` to build the project, and `gradle run` to run the project.

Make your changes, and then send us a pull request. If you didn't break the
build, and it seems like a nice change, then we'll accept it!

To access the server which you're running you will need to use a client.
At the moment, the only client is the
[web client](https://github.com/haver-chat/haver-web-client), which is in
active development.

Happy hacking!
