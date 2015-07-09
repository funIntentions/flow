application version 7.6.2015

|-----------|
| Interface |
|-----------|

========
Menu Bar
========

+ File
- Load Default
	Loads the default structure templates, clears all changes you've made and resets the simulation.
- Load File
	Loads in a .grid file which is a file containing a previously created simulation
- Save File:
	Saves all your changes and created structures to a .grid file which you can later load from
- Quit: 
	Shuts down the application

+ Edit
- Remove Structure
	Removes the selected world structure from the World Structures Panel, removing it from the simulation
	Note: this button will be greyed out if no World Structure is selected
- Add Structure
	Adds the selected world structure to the World Structures Panel, adding it to the simulation
	Note: this button will be greyed out if no Template Structure is selected
- Edit Structure
	Opens up the Editing Structures window where you can edit the selected Structure
	Note: this button will be greyed out if no Structure is selected

+ View (Note: since removing the ontology, this menu isn't very helpful and will likely be removed)
- Template Structures
	Hides the Template Structures Panel
- World Structures
	Hides the World Structures Panel

========
Tool Bar
========

+ Structure Editing Tools (Note: these have the same functionality as their respective menu item)
- Edit Structure
	Opens up the Editing Structures window where you can edit the selected Structure
- Add Structure
	Adds the selected world structure to the World Structures Panel, adding it to the simulation
	Note: this button wont appear until a Template Structure is selected
- Remove Structure
	Removes the selected world structure from the World Structures Panel, removing it from the simulation
	Note: this button wont appear until a World Structure is selected

+ Simulation Tools
- Run
	Starts the simulation
- Pause
	Pauses the simulation 
- Reset
	Resets the simulation to a state where no time has passed

=========================
Template Structures Panel
=========================

+ This panel simply displays a list of template structures which can be added to the simulation world

+ Any world structures created from a template structure will initially have all the same characteristics
	of that template
	
+ Clicking on a structure in the list selects it

======================
World Structures Panel
======================

+ This panel simply displays a list of world structures which are active in the simulation

+ Clicking on a structure in the list selects it

==============
Graphics Panel
==============

+ Displays images of structures currently in the world

Note: presently this panel is pretty bare-bones and only two images (House and Power Plant) are available 

=====================
Simulation Info Panel
=====================

+ Update Rate (Note this is the drop-down combo-box/there is currently no label for this)
	Specifies how quickly the simulation should update, for example: setting the update rate to WEEKS
	will mean the simulation will update one week per second.
+ Usage
	The kWh of energy used by applications within structures in the World Structures Panel
+ Cost
	The total cost of energy production needed to meet energy usage
+ Emissions
	The grams per kWh of energy produced to meet energy usage
+ Time
	The total amount of time that has elapsed in the simulation since it's start.
	It's displayed in this format -> Hours:Seconds:Days:Weeks:Months:Years
+ Time Limit (Days)
	Specifies in days how long the simulation should run for

|------------------------------------------|
| Editing Structures (In Structure Editor) |
|------------------------------------------|

Upon clicking either the Edit Structure button on the Tool Bar or the Menu Item under the edit menu, 
the Structure Editor will appear.

+ Information
- Structure's Name (For all Structures)
	The name of the structure which will be used to identify it in the simulation
	Cannot be blank
- Number of Units (For Apartment Building, Office Building and Mall)
	Not yet factored into the simulation
	
+ Building Properties
- Each structure has a collection of properties that can be adjusted
- The properties being used at the moment are the following:
	xCoordinate (All Structures) - Used to position this structures image in the Graphics Panel
	yCoordinate (All Structures) - Used to position this structures image in the Graphics Panel
	EmissionRate (Power Plant) - the grams per kWh of green house gasses that the power plant produces
	ProductionCost (Power Plant) - the $ per kWh that the power plant's production costs

+ Building Devices
- Each building can own a collection of appliances, energy sources and/or energy storage devices
- Using the four buttons in the bottom left new devices can be added or removed
	The Remove Device button requires that the device being removed is selected
(Note: Currently only the appliances are actually used in the simulation)
		
+ Device Properties
- Each device has a collection of properties that can be adjusted
- To see the properties of a device select it from a tab under Building Devices 
- The properties being used at the moment are the following:
	AverageConsumption (Appliances) - the Watts used by this appliance
- Note that you can also change a devices name by double clicking it in the Building Devices list

+ Daily Device Usage
Selecting a device will also display the daily time spans which it is active during (if any)
- Add Time Span button will add a new time span From 00:00 to 00:00 (Format is Hours:Minutes in military time)
	To edit this new time span just double click of either the value in the From or To column.
- Remove Time Span button will remove the time span in the row that is currently selected in the Daily Device Usage Table


|-------------------------|
| A Functional Simulation |
|-------------------------|
1. You'll need to create a home with a least one appliance
	1.1 For that appliance(s) you'll have to get it's average consumption property (Watts need) to something other than 0
	1.2 For that appliance(s) you'll need to specify some time spans that it will be active during 
		For Example: Add Time Span and set the To value to 12:00 and leave From at 00:00 - the device will be active from midnight to noon
2. You'll need to create a power plant
	2.1 For that power plant you'll need to set the production cost ($ per kWh) to something other than 0
	2.3 For that power plant you'll need to set the emission rate (g per kWh) to something other than 0
3. You'll need to specify the time limit for the simulation in the Simulation Info panel (by default it's set to 1 day which is fine)
4. You'll need to specify the update rate for the simulation in the Simulation Info panel (by default it's seconds which is quite slow if you don't want to wait all day)
5. You can now press Run on the tool bar and the simulation will begin
6. You should now see the values in the Simulation Info Panel being updated


|------------------------|
| Some Known Issues/Bugs |
|------------------------|
- Editing Time Spans isn't very robust, restrictions are needed to:
	- prevent multiple time spans that are the same or those who overlap
- In the Time Limit Field letters can be enter which will throw errors

- negative average consumptions shouldn't be allowed
- The Structure Editor Window for power plants should be have a smaller size/less blank space.
