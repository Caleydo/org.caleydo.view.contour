Caleydo View Template
=========================

1. create a new repository org.caleydo.view.<name>
2. clone the repository
3. create a new develop branch ```git checkout -b develop```
3. merge this template into your repository: 
   ~~~
   git remote add template git@github.com:Caleydo/org.caleydo.view.template.git
   git fetch template
   git merge template/develop
   ~~~
3. run ant: ```ant -f configure.bat``` either via Eclipse or direct from the command line
4. follow the instruction
<<<<<<< HEAD
5. push the code to a new remote develop branch: ```git push origin develop:develop```

Alternative
1. fork this repository
2. go to step 3
=======


Conventions
========================
This project follows the Maven [standard directory layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) mixed with some eclipse specifics

That means: 
 * put java source code in: ```src/main/java```
 * put resources referenced by java, e.g. icons, in: ```src/main/resources``` and 
   reference them using [Class.getResource](http://docs.oracle.com/javase/7/docs/api/java/lang/Class.html#getResource(java.lang.String)) or [Class.getResourceAsStream](http://docs.oracle.com/javase/7/docs/api/java/lang/Class.html#getResourceAsStream(java.lang.String) )
 * put eclipse only resources, e.g. view icons, in ```resources```

Icon Resolutions
=======================
The eclipse icons should have a resolution of 16x16 in the PNG format. 
In constrast to that the Java / OpenGL icons should have a resolution of 32x32 allowing zooming without generating artifacts.
>>>>>>> develop
