steps
============

Contribution
----------

### Setup
If you want to contribute, the source is built using Maven and Google Web Toolkit.
In Eclipse you can therefore use:
* m2e - Maven Integration for Eclipse (e.g. for Juno version: http://download.eclipse.org/releases/juno)
* Google Plugin for Eclipse (https://developers.google.com/eclipse/)

and 

1. configure your Eclipse project to be a Maven Project and to use Google Web Toolkit (2.6)
2. run `mvn package` to set up `/src/main/webapp/` files for GWT dev mode. Run again for changes in the directory

Please [configure your git](http://git-scm.com/book/en/Customizing-Git-Git-Configuration) for this repository as:
* `core.autocrlf` true if you are on Windows 
* or `core.autocrlf input` if you are on a Unix-type OS

Permanent ontologies reference classes defined elsewhere. In order to 'extract modules' for these classes from their original ontology these ontologies have to be loaded. However, not all of these ontologies are specified with an import declaration. Somtimes a rdfs:defined_by or a obo:IAO_0000412 annotation is used. Even sometimes no indication whatsoever is given. One can still guess the source ontology from the class' IRI. It may be useful/necessary to avoid problems to prescan all permanent ontologies for these cases and add these ontologies to the pre-loaded set 
Also see edu.arizona.biosemantics.oto2.steps.server.persist.file.OWLOntologyRetriever


### Run Dev Mode

#### Class
`com.google.gwt.dev.DevMode`

#### Arguments
`-remoteUI "${gwt_remote_ui_server_port}:${unique_id}" -startupUrl index.html -logLevel INFO -port auto -codeServerPort auto -war **full_path_to_your_git_dir**\oto2\steps\target\steps-0.0.1-SNAPSHOT edu.arizona.biosemantics.oto2.steps.OtoSteps`
