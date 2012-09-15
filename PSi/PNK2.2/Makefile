
# Please rename your PNK tool specification
TOOLSPECDIR=toolSpecifications/
TOOLSPEC=$(TOOLSPECDIR)toolSpecification.xml
TESTTOOLSPEC=$(TOOLSPECDIR)toolSpecificationTest.xml

# Please locate your JAVA path:
JAVADIR=

# Rename your Java interpreter and your Java compiler
JAVA=$(JAVADIR)java
JAVAC=$(JAVADIR)javac
JAVADOC=$(JAVADIR)javadoc
JAR=$(JAVADIR)jar

# the destination and the links of the documentation
DOCDIR=doc
DOCDEST=-d $(DOCDIR)

DOCLINK=-link doc \
        -link http://java.sun.com/products/jdk/1.2/docs/api \
	-link http://java.sun.com/xml/jaxp-1.1/docs/api

CLASSPATH=.:crimson.jar:jaxp.jar

#The path for all java files
VPATH := de/huberlin/informatik/pnk/appControl:\
	de/huberlin/informatik/pnk/appControl/base:\
	de/huberlin/informatik/pnk/app:\
	de/huberlin/informatik/pnk/app/base:\
	de/huberlin/informatik/pnk/tools:\
        de/huberlin/informatik/pnk/tools/base:\
	de/huberlin/informatik/pnk/editor:\
	de/huberlin/informatik/pnk/exceptions:\
	de/huberlin/informatik/pnk/kernel:\
	de/huberlin/informatik/pnk/kernel/base:\
	de/huberlin/informatik/pnk/netElementExtensions/base:\
	de/huberlin/informatik/pnk/netElementExtensions/llNet:\
	de/huberlin/informatik/pnk/netElementExtensions/hlNet:\
        de/huberlin/informatik/pnk/netElementExtensions/PNCube:
empty:=
comma:= ,
space:= $(empty) $(empty)
dirs := $(subst :,$(space),$(VPATH))
packs:= $(subst /,.,$(dirs))

SRCS:= $(foreach dir,$(dirs),$(wildcard $(dir)/*.java))

# Now, given these sources we want to compute the class file names and
# the API-Doc file names
CLASSES:= $(addsuffix .class, $(basename $(SRCS)))
#APIDOCS:= $(addsuffix .html, $(doc/basename $(SRCS)))

# Now we can compute our dependencies

all:	$(CLASSES)

run:	all
	$(JAVA) -classpath $(CLASSPATH) de.huberlin.informatik.pnk.appControl.ApplicationControl $(TOOLSPEC)

test:	all
	$(JAVA) -classpath $(CLASSPATH) de.huberlin.informatik.pnk.appControl.ApplicationControl $(TESTTOOLSPEC) -dt 3

clean:
	@@for p in $(dirs); do \
                echo '### cleaning package ' $(p)$$p; \
                rm -f $(p)$$p/*.class;\
        done
	rm -f PNK2.jar PNK2.tgz

apidoc: 
	touch $(DOCDIR)/package-list;\
	$(JAVADOC) $(DOCDEST) $(DOCLINK) $(packs)


dos:
	@@for p in $(dirs); do \
                echo 'dos in package '$(p)$$p; \
                recode lat1:ibmpc $(p)$$p/*.java; \
        done

dos2:
	@@for p in $(dirs); do \
                echo 'dos in package '$(p)$$p; \
                recode lat1..ibmpc $(p)$$p/*.java; \
        done

unix:
	@@for p in $(dirs); do \
                echo 'unix in package '$(p)$$p; \
                recode ibmpc:lat1 $(p)$$p/*.java; \
        done

unix2:
	@@for p in $(dirs); do \
                echo 'unix in package '$(p)$$p; \
                recode ibmpc..lat1 $(p)$$p/*.java; \
        done

# Das jar-File enthält alle class-Dateien sowie eine Metainf
# Start mit java -jar PNK2.jar
jar:	all
	$(JAR) cmf META-INF/MANIFEST.MF PNK2classes.jar `find de -name '*.class'` config/
	$(JAR) cmf META-INF/MANIFEST.MF PNK2sources.jar `find de -name '*.java'` config/
	rm -r de
	
# Das tgz-File enthält das jar-File sowie die Quellen und alles Zubehör...
tgz:	jar
	@@tar cpfz PNK2.tgz `find de -type f -a ! -name '*.class'` netTypeSpecifications sampleNets toolSpecifications config crimson.jar jaxp.jar Makefile PNK2.jar META-INF

%.class:%.java
	$(JAVAC) -classpath $(CLASSPATH) -g -deprecation $<





