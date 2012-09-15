
   The Petri Net Kernel (PNK) version 2.0 provides an infrastrucure for
   building Petri net tools.
   
   Copyright (C) 1999-2001 Petri Net Kernel Team 
                           (Humboldt-University Berlin, Germany)
   
   pnk@informatik.hu-berlin.de
   
   
   Table of Contents
   -----------------
  
     * License Agreement
     * Quick Start
          + Download and Run
          + First steps - Open, Edit and Save Nets
          + Start a first Simulator Session
     * Distributed Files

   
   License Agreement
   -----------------
  
   The PNK version 2.0 is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   (see file gpl.txt) as published by the Free Software
   Foundation; version 2 of the license.

   Furthermore, this product includes software developed by the 
   "Apache Software Foundation" (http://www.apache.org/)
   as well as software developed by the
   "Sun Microsystems, Inc." (http://www.sun.com/).

   The PNK is distributed in the hope that it will be
   useful, but WITHOUT ANY WARRANTY.
   
   You are NOT ALLOWED to CHANGE THE ORIGINAL COPYRIGHT NOTICE. See the
   GNU Library General Public License for more details. You should have
   received a copy of the GNU Library General Public License along with
   the PNK; if not see http://www.gnu.org/. An application using the
   Petri Net Kernel MUST GIVE NOTICE OF THIS USE. Please contact
   ag-pnk@informatik.hu-berlin.de to notify this application.

   
   Quick Start
   -----------

   This is just a crash course to get the Petrinet Kernel running. It
   should give you an overview how to use the Petrinet Kernel. I'm
   leaving out some important details which will be added soon ...
   
   NOTE: It is important that you have installed the jdk1.2.x to use the
   Petrinet Kernel. Otherwise download it from java.sun.com. If you don't
   know which jdk version is installed on your system, you may check this
   by calling the java interpreter's -version option.
   
   >java -version

   
   Download and Run
   ----------------
 
   Download the latest PNK2alpha.jar file. Extract the archive on the command
   line by typing
   
   >jar xvf PNK2alpha.jar
   
   This creates a new directory PNK2/ containing the source and class
   files of the Petrinet Kernel. Now you may change to PNK2/ directory
   
   >cd PNK2
   
   and take a look at it's contents. You should find something like this

     * jaxp.jar - api for reading and writing xml files
     * crimson.jar - api for reading and writing xml files
     * PNK2.jar - archive of the Petrinet Kernel's class files
     * PNK2sources.jar - archive of the Petrinet Kernel's source files
     * Makefile - with this you may compile sources by hand
     * sampleNets - contains some saved netexamples
     * netTypeSpecifications - contains examples for a net's specification
     * toolSpecification - contains some toolspecification examples
     * icons - icons used by applications
       
   Then start up Petrinet Kernel using the -jar option of the java
   interpreter.
   
   >java -jar PNK2.jar
   
   You may also compile sources by hand. Therefore you possibly have to
   extract a source file archive.
   
   >jar xvf PNK2sources.jar
   
   This command installs a directory tree of source files. Now take
   notice of the makefile in the PNK2/ root directory. Modify it to your
   requirements and enter
   
   >make run
   
   This command should compile all files and run the Petrinet Kernel. If
   it does not, please check the path to your java2 interpreter and java2
   compiler. You may edit the path in the headline of the makefile.
   Verify also the correctness of the CLASSPATH variable. It should at
   least point to the crimson.jar and jaxp.jar archives distributed with
   the pnk.jar archive. These two file archives are necessary for loading
   and writing xml and pnml files.
   
   If the makefile doesn't work you may generally start the Petrinet
   Kernel by typing a commandline like this:
   
   >java -classpath .:jaxp.jar:crimson.jar \
       de.huberlin.informatik.pnk.appControl.ApplicationControl [<toolspec>]
   
   In this case it is important that you are currently in the root
   directory of the Petrinet Kernel. The `toolspec` argument is 
   optional and should point to a valid `toolSpecification.xml` file.
   This file contains data of known nettypes and their applications.
   It is recommended that you compose your own specification file.
   Therefore you find some examples in the `toolSpecifications/` directory.

   
   First steps - Open, Edit and Save Nets
   --------------------------------------
 
   Now I will give you a short tutorial how you can play around with the
   Petrinet Kernel. In this section I tell you how you may open or edit
   nets and how you may invoke applications to work on your net. When
   launching the Petrinet Kernel the ApplicationControl's window should
   appear.
   
   In the ApplicationControl's menubar you find a File menu for opening
   existing nets, creating new nets and saving nets. There should also be
   a Net menu for starting applications or switching between them. At
   first you have to choose your nettype. You may open the File menu. If
   you select the File -> new Net menuentry a submenu with a list of
   different nettypes appears. Click on one of these menuitems. You can
   also select the File -> Open menuentry to open an existing net. Some
   interesting netexamples are stored in the sampleNets/ directory.
   
   As default a editor start's to display the net. Everytime you invoke
   an application it's menu is shown in the ApplicationControl's menubar.
   So now you should find there the editor's menu. You may add objects
   like places, transitions and arcs to the net.
   
   In the editor's menu you select the Place checkbox or the Transition
   checkbox. Then click on an open editorpage. Every mouseclick should
   create a new object. Then choose the Arc checkbox and mouseclick on a
   place. The place should change it's color indicating that it is
   selected as an arc's initialnode. Now mouseclick a transition and a
   new arc appears. You may want to drag around some places and
   transitions to make your net look lovely. You achieve this by
   mouseclicking in an object and hold the mousebutton pressed while
   moving your mouse. After this you usually want edit the extensions of
   some objects. For example set the name and marking of places, the
   inscriptions of several arcs or the guards of transition. Which of
   these extensions exists depends on the nettype. To edit an extension
   you choose the Edit checkbox in editor's popupmenu. Now a mouseclick
   on a place, transition or arc will open a dialog frame where you may
   set new values for the objects extensions. Then it is recommended that
   you save your net with the File -> save or File -> save as menu and
   choose ApplicationControl's Net menu to start a further application
   for example a simulator.

   
   Start a first Simulator Session
   ------------------------------- 

   Choose Net -> Start Application -> Simulator. This creates a new
   instance of the simulator. Now the simulator's menu should appear in
   the ApplicationControl's menubar. You should also see a dialog frame
   with a CANCEL button. To run the simulator choose the Simulator ->
   Start menu from ApplicationControl's menubar. If there is any
   concessioned transition in the net the simulator will emphasize it in
   the editor. You may fire a concessioned transition by mouseclicking
   into it. Continue these steps until you are tired or there is no
   concessioned transition anymore. In the first case press the CANCEL
   button in the simulator's dialog frame. (It is recommended that you
   don't choose Simulator -> Stop from ApplicationControl's menubar,
   because it's not yet correctly implemented.) In the last case the
   simulator stops automatically.


   Distributed Files
   -----------------
 
   Root directory:

        ./Makefile
        ./README.txt
        ./PNK2.jar
        ./PNK2src.jar
        ./jaxp.jar
        ./crimson.jar
        ./sampleNets
        ./netTypeSpecifications
        ./toolSpecifications  
        ./gpl.txt
        ./License-ASF
        ./License-RI.html

   Examples for toolspecifications ...
 
     * toolSpecifications/:

       +  net2pnmlApp.xml
       +  toolSpecification.dtd
       +  toolSpecification.xml
       +  toolSpecification1.xml
       +  toolSpecification2.xml
       +  toolSpecification3.xml     

   Examples for nettypespecifications ...

     * netTypeSpecifications/:

       +  BlackTokenNet.xml
       +  Echo.xml
       +  GHSGraph.xml
       +  HLNet.xml
       +  PTNet.xml
       +  bagNet.xml
       +  dawnGuard.xml
       +  graph.xml
       +  netTypeSpecification.dtd
       +  netTypesPytJava.table
       +  signatureTable.dtd
       +  signatureTable.xml
       +  subrange.xml
       +  timedNet.xml     


   There are also some examples for nets ...       

     * sampleNets/:

       +  HLDinner.net
       +  ProducerConsumerSystem.pnml
       +  ProducerConsumerSystem1.pnml
       +  ProducerConsumerSystem2.pnml
       +  ProducerConsumerSystem3Pages.pnml
       +  bag.pnml
       +  blackToken.pnml
       +  connectivity.pnml
       +  connectivity.xml
       +  dinner.net
       +  echo.pnml
       +  echoInit.pnml
       +  maximumFinding.pnml
       +  maximumFinding.xml
       +  ptNet.pnml
       +  ptNet.xml
       +  shortestPath.pnml
       +  shortestPath.xml
       +  subrange.pnml
       +  timeNet.pnml
       +  wzk.net                      

   The Petrinet Kernel's sources are a bunch of packages ...

     * de.huberlin.informatik.pnk.kernel:

       +  Arc.java
       +  BlockStructure.java
       +  Edge.java
       +  Extendable.java
       +  Extension.java
       +  Graph.java
       +  Kerntest.java
       +  Member.java
       +  NameExtension.java
       +  Net.java
       +  Node.java
       +  Place.java
       +  PlaceArc.java
       +  Specification.java
       +  SpecificationTable.java
       +  Transition.java
       +  TransitionArc.java         

     * de.huberlin.informatik.pnk.kernel.base:

       +  ActionObject.java
       +  ChangeExtension.java
       +  ChangeSourceAction.java
       +  ChangeTargetAction.java
       +  DeleteAction.java
       +  FlattenAction.java
       +  JoinInterfaceNodeAction.java
       +  NetObservable.java
       +  NetObserver.java
       +  NewArcAction.java
       +  NewBlockAction.java
       +  NewNetAction.java
       +  NewPlaceAction.java
       +  NewPlaceArcAction.java
       +  NewTransitionAction.java
       +  NewTransitionArcAction.java
       +  RegisterInterfaceAction.java
       +  RegisterSonAction.java
       +  SplitInterfaceAction.java
       +  UnregisterInterfaceAction.java
       +  UnregisterSonAction.java         
 
     * de.huberlin.informatik.pnk.app:

       +  Application.java
       +  ApplicationB.java
       +  BspApplication1.java
       +  BspApplication2.java
       +  GHSSimulator.java
       +  GraphAlgorithmSimulator.java
       +  MarkingsToInitial.java
       +  Simulator.java           

     * de.huberlin.informatik.pnk.app.base:

       +  AnnotateObjectsAction.java
       +  ApplicationACInterface.java
       +  ApplicationAWInterface.java
       +  ApplicationNetDialog.java
       +  ApplicationRequests.java
       +  EmphasizeObjectsAction.java
       +  MetaActionObject.java
       +  MetaApplication.java
       +  MetaJFrame.java
       +  NetObserver.java
       +  ResetAnnotationsAction.java
       +  ResetEmphasizeAction.java
       +  SelectObjectAction.java
       +  SelectObjectsAction.java
       +  StructuredNetObserver.java
       +  UnAnnotateObjectsAction.java
       +  UnEmphasizeObjectsAction.java

     * de.huberlin.informatik.pnk.appControl:

       +  AAObject.java
       +  ACResources.java
       +  ANObject.java
       +  ATObject.java
       +  ApplicationControl.java
       +  ApplicationControlMenu.java
       +  CTObject.java
       +  IOTObject.java
       +  InOut.java
       +  NFObject.java
       +  NTObject.java
       +  NetInOut.java
       +  NetParseException.java
       +  PNKClassLoader.java
       +  PNKDialog.java
       +  PnmlInOut.java
       +  TestInOut.java
       +  ToolSpecification.java
       +  Viewer.java

     * de.huberlin.informatik.pnk.appControl.base:

       +  ACApplicationInterface.java
       +  PnkFileFilter.java

     * de.huberlin.informatik.pnk.netElementExtensions.base:

       +  FiringRule.java
       +  Inscription.java
       +  Marking.java
       +  Mode.java
       +  Type.java

     * de.huberlin.informatik.pnk.editor:

       +  AllSelectedDialog.java
       +  Annotation.java
       +  Arc.java
       +  Draw.java
       +  Edge.java
       +  EditDialog.java
       +  Editor.java
       +  EditorMenu.java
       +  EditorWindow.java
       +  Extension.java
       +  GraphProxy.java
       +  GruenLookAndFeel.java
       +  Label.java
       +  LogoWin.java
       +  MemberSprite.java
       +  MemberSpriteNode.java
       +  NetLoader.java
       +  NetWriter.java
       +  Node.java
       +  Page.java
       +  PageMouseListener.java
       +  PageVector.java
       +  Place.java
       +  PlaceArc.java
       +  ReferenceTable.java
       +  SelectDialog.java
       +  Sprite.java
       +  SpriteVector.java
       +  Transition.java
       +  TransitionArc.java
       +  ViewPane.java

     * de.huberlin.informatik.pnk.exceptions:

       +  ExtensionValueException.java
       +  KernelUseException.java
       +  NetSpecificationException.java
   
