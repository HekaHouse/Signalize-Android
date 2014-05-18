Signalize
=========

A suite of Android applications
including MyVoice, MIRA,
 and Perspectives


MIRA current state of the art

MyVoice provides an interface to MIRA. 
-A user is able to type or speak and MIRA responds in kind.
-Prior to generating a response MIRA builds her "brain" by reading the AIML files and either generating sentiment model or reading the serialized form
the AIML and sentiment modeling data is stored under assets and has to be processed in a background thread
-initial modeling can take some time depending on device, it is recommended build and deploy without debugger and then attach after this part is complete
-in parallel MIRA initializes the speech recognition and text to speech services
-when these processes are complete MIRA signals she is ready by speaking and begins to listen
-MIRA listens for the phrase "Hello Mira" to begin a conversation
-saying "goodbye Mira" signals the conversation is ending
-one issue with using Mira as a trigger word is its auditory ambiguity with the word mirror

root.gast
-this library is code from the book Android Sensor Programming:
https://market.android.com/details?id=book-yi-WNEGcO0EC
-This is the current underlying system managing the speech recognition
MiraActivator in package ppc.signalize.mira.nervous is an implementation of this code
-MiraActivator extends SpeechActivator from root.gast
-MiraAbstractActivity also extends root.gast
-SpeechRecognizingActivity provides boiler plate code for the Activity
-SpeechRecognizingActivity is a basic conceptual approach that needs to be implemented as SpeechRecognizingService
-MyVoiceActivity is a concrete implementation of this approach in activity form

Program AB
-this library is the foundation of the chatbot response AI
package is org.alicebot.ab
-Ghost is extension of Bot.java
-It overrides the methods from Bot that consume the AIML
-this provides a streaming XML reader as opposed to the original DOM model which was far too slow on Android
-The package also has new classes to handle multi threading of the consumption process

