SOURCES=ImagePimp.java AINet.java
OBJECTS=$(SOURCES:.java=.class)
AIS_OBJS=AINet\$$Antibody.class  AINet\$$Antigen.class  AINet\$$Cell.class  \
	AINet.class


all: build package

package: AINet.jar

AINet.jar: AINet.class
	jar cfm AINet.jar Manifest.txt $(AIS_OBJS)

build: clean $(OBJECTS)	

%.class: %.java
	javac $<

%: %.java
	@echo "Building $@"
	javac $<

clean:
	rm -rf *.class

benchmarks: 
	utils/benchmarks.sh
