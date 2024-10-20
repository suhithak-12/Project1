#Suhithareddy Kantareddy \
 * 09/30/2024 \
 * CSE 3053 \
 * Project 1 \
 * Makefile \
 * errors: None that i know of. \
 
 #Variables
JAVAC = javac
JAVA = java
JFLAGS = -g
SOURCES = Main.java NodeA.java NodeB.java NodeC.java
CLASSES = $(SOURCES:.java=.class)
MAIN = Main

# Default target
default: $(CLASSES)

# Compile java files to class files
%.class: %.java
	$(JAVAC) $(JFLAGS) $<

# Run the program
run: $(CLASSES)
	$(JAVA) $(MAIN)

# Clean up class files
clean:
	rm -f *.class
