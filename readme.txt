1. Make sure to have loaded the three databases with the following names: convinceme, createdebate, fourforums. The databases are available for download at: https://nlds.soe.ucsc.edu/iac2
2. Create a ./project.properties file in the root directory of the project set up with the following three variables:
	DB_ADDRESS=jdbc:mysql://address (include full database address)
	DB_USER=username (where username is the full username to log in to the database)
	DB_PASSWORD=password (where password is the full password to log in to the database)
	
	FILE_OUTPUT_PATH= the location on your computer where the .json files representing each forum post should be saved (these will then be used in the accompanying Python project for classifier training)

	[You can use the project.properties.sample file by removing the .sample extension and filling in the required information within it]