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
5. push the code to a new remote develop branch: ```git push origin develop:develop```

Alternative
1. fork this repository
2. go to step 3
