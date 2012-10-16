SOURCES=ImagePimp.java AINet.java
OBJECTS=$(SOURCES:.java=.class)

all: build

build: clean $(OBJECTS)	

%.class: %.java
	javac $<

%: %.java
	@echo "Building $@"
	javac $<

clean:
	rm -rf *.class
