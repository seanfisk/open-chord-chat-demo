GVSU CIS656 Distributed Systems Lab 3
=====================================

This lab is a simple chat client and presence server implemented using [OpenChord](http://open-chord.sourceforge.net/), a Distributed Hash Table implementation in Java.

This project is built with [Apache Buildr](http://buildr.apache.org/), a build system for Java.

Features
--------

* Readline-like line editing with [JLine](http://jline.sourceforge.net/)
* Auto-completion for all commands and for known friends
* Argument parsing with 
* Bulletproof code

Prerequisites - Build System
----------------------------

If you use [Ruby Gems](http://rubygems.org/), the install is incredibly easy:

	gem install buildr

Otherwise, consult the [Buildr installation documentation](http://buildr.apache.org/installing.html) for ways to install.

Compile and Run
---------------

* Grab the code:

		:::bash
		git clone https://seanfisk@git.code.sf.net/p/sf-gvsu-cis656/lab3/code sf-gvsu-cis656-lab3-code
		cd sf-gvsu-cis656-lab3-code

* Start by running the master node. This is the node that all other nodes will bootstrap when entering the network.

		buildr talk-chord:run-master

* Start a new shells and run as many other clients as you want. (I use [tmux](http://tmux.sourceforge.net/) for this task)

		buildr talk-chord:run-normal
		
* If you quit the master node, new nodes will not be able to join the network. But the current nodes can continue to communicate.
