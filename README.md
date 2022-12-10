# zome3d-cwbennett

This is the code for a student project at the Colorado School of Mines, sponsored by Zometool and Paul Hildebrandt.
The authors were C.W. Bennett, Mike Wilson, Luke Misgen, and Francisco Garcia.
According to Tom Darrow, another Mines student and friend of Paul's:

> The idea started in winter/spring of 2002 and the proposal was written in April.  It would have been a team of juniors, class of 2003.
> Mines ran 2 16-week semesters from late August to Christmas, and then January to the first week of May.
> Then we had "summer session" for juniors in the math/cs department that was a single 6-week project from an industry source.
>
> Having done my own summer session the year before, I was like "this would be a great way to get some better Zome software",
> and I called Paul and sold him on the chance.  And I advised for the period of time I was still in the Denver area.
>
>I believe most of CW's team also went to the colloquium talk I did that introduced Zome to the department. (Paul was there with some free samples.)

# Build and Run

I have this working in my local Eclipse by providing a `libs` folder with JARs for Java3d 1.6.0, as you can see in the `.classpath` file.
It only works this way with Java 8, or at least I know it does not run if I switch the project JRE to be Java 17,
where there is no `AppletViewer` available, apparently.
