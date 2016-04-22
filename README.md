# Local-Remote Mock Service using Raml-Vertx.

For a Detailed Explanation of RAML and Usage of this Mock Service, refer to [wiki-document](https://github.com/bandikishores/mock-service-raml-vertx/wiki/Mock-Service-and-RAML)

Exploring Vertx and RAML - To create a Mock Service which takes RAML files as input, parses the RAML and creates the web services based on the resources available in RAML File. Finally returns the example section as output when the URL is hit.

* Creates a HTTP connection for the RAML file defined.<br />
* Reads the contents of the RAML file and creates mock web services for the path defined in RAML.<br />
* On hitting/accesing the URL - 
 + If found, It returns the example provided in the RAML as the output.<br />
 + If not found, then based on the base URI 
   - If any externals servers have been added the call will be forwarded to respective servers and the output of external server will be transfered to client. <br/>


You can download the [mock service jar](https://drive.google.com/file/d/0B0l-Cm-uMwrBQnFjMTQ3dElKaGM/view?usp=sharing) <br/>


### 1. To start the service using command prompt:
> java -jar <Path To Jar> [Folder Path where RAMLs are present]

Option:
	Folder Path for RAML - This is optional.

Ex : 
> java -jar vertx/target/mock-service.jar /home/kishore/mygit/vertx/vertx/src/main/resources/raml/

<br/>

After this command execution, the server starts at port 4123. It can be accessed using 
> http://localhost:4123/[Resource-Path-Including-Base-Path]  

> Ex: http://localhost:4123/bankService/getAccounts.json 

  * If the response from the example section provided appears then the service is working fine. 
    + Alternatively, you can use Postman to check the response based on get/post method types.
    
<br/>

### 2. To Add an External Service: <br/>
Access the Admin URL at
> http://localhost:4124/

Say the resource "/getAccounts.json" was not defined in the RAML contract. In which case you would like to get this info from third party/External server.
Then go to Admin URL mentioned above and add the below details
> For URL http://mybankdomain.com:8080/bankService/getAccounts.json <br/>
1. BaseURI - bankService <br/>
2. Port - 8080 <br/>
3. Hostname - mybankdomain.com <br/>

Now make the request and you'll be able to get the contents from that server as well.
<br/><br/>

### 3. Additional Configuration when building in local
Add below repo and profiles in your .m2/settings.xml for downloading raml parsers

	<repository>
                <id>mulesoft-releases</id>
                <name>MuleSoft Repository</name>
                <url>http://repository.mulesoft.org/releases/</url>
                <layout>default</layout>
            </repository>
            <repository>
                <id>mulesoft-snapshots</id>
                <name>MuleSoft Snapshot Repository</name>
                <url>http://repository.mulesoft.org/snapshots/</url>
                <layout>default</layout>
            </repository>





	<profile>
            <id>Mule</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>MuleRepository</id>
                    <name>MuleRepository</name>
                    <url>https://repository.mulesoft.org/nexus-ee/content/repositories/releases-ee/</url>
                    <layout>default</layout>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>



  	<activeProfiles>
	     <activeProfile>Mule</activeProfile>
	</activeProfiles>

