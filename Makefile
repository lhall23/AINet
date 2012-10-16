SOURCES=ImagePimp.java 
OBJECTS=$(SOURCES:.java=.class)

all: build

build: clean $(OBJECTS)	

%.class: %.java
	javac $<

clean:
	rm -rf *.class
