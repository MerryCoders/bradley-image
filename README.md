bradley-image
=============
Run/Debug
Command Line:  run-app -debug -reloading
VM Options: -XX:MaxPermSize=512m -Xmx1024m

Testapp/Debug
Command Line:  test-app -debug
VM Options: -XX:MaxPermSize=512m -Xmx1024m

If you wish to depbug the app in IntelliJ while running in forked execution mode:
1.  Modify BuildConfig by uncommenting the forked execution line and commenting out the original
2.  In Intellij  go to "Run / Edit Configurations"
3.  Click + then "Remote" and name it whatever  you want (example "Grails Remote")
4.  Press ok
5.  Then from the command line do: grails run-app --debug-fork
6.  Attach your debugger by running your "Grails Remote" config. Done.