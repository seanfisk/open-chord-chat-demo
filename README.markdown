Open Chord Chat Demo
====================

This lab is a simple chat client and presence server implemented using [OpenChord](http://open-chord.sourceforge.net/), a Distributed Hash Table implementation in Java. It was created for the GVSU gradudate class CIS 656 Distributed Systems with Dr. Jonathan Engelsma.

This project is built with [Apache Buildr](http://buildr.apache.org/), a build system for Java.

Features
--------

* Readline-like line editing with [JLine](http://jline.sourceforge.net/).
* Auto-completion for all commands and for known friends.
* Argument parsing with [JCommander](http://jcommander.org/).

Prerequisites - Build System
----------------------------

If you use [Ruby Gems](http://rubygems.org/), the install is incredibly easy:

	gem install buildr

Otherwise, consult the [Buildr installation documentation](http://buildr.apache.org/installing.html) for ways to install.

Compile and Run
---------------

* Grab the code:

		git clone https://github.com/seanfisk/open-chord-chat-demo.git
		cd open-chord-chat-demo

* Start by running the master node. This is the node that all other nodes will bootstrap when entering the network.

		buildr talk-chord:run-master

* Start a new shells and run as many other clients as you want. (I use [tmux](http://tmux.sourceforge.net/) for this task)

		buildr talk-chord:run-normal

* If you quit the master node, new nodes will not be able to join the network. But the current nodes can continue to communicate.
