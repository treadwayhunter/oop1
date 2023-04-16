# How to submit projects
* Pull latest class updates as 
``` 
cd CS3354
cd 2023Spring
git pull
cd ..
```

**Note: make sure to pull your updates if you are contributing to the project from different locations**
``` 
cd NetID
git pull
cd ..
```

2. Copy instructions and solution 
* FIRST TIME: copy entire **project/projects/src/main/java/project1** folder under your NetID folder 
* EVERY OTHER TIME: only copy the new material not to overwrite your own work 
  * copy project material from **CS3354/2023Spring/project/projects/src/main/java/project1** folder 
  * paste to **CS3354/NetID/projects/src/main/java/project1** folder 

3. Make changes to Java code in **NetID** repository 
* If you are using VSCode/Maven, right click in this new local **NetID/project1** directory and choose ```Open with VSCode``` 
* If you are not, direct your IDE to pom.xml Maven project in the **NetID/project1** folder
* If using commandline or text editors, make changes to java code in **NetID/src/main/java/ folder and save changes 
  
4. check in changes to  **NetID** repository - make sure you are in the **NetID** folder for git commandline:

```
cd 2023Spring/NetID
git add projects/src/main/java/project1/*.java
git commit -m "project1 java updates"
gitk
```
gitk will show you the status, close it to continue
```
git push origin
```

5. **Repeat step 4 often to save your work until done.**
  * it allows you to re-trace your steps or overwrite the code by mistake
  
