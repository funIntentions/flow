application version 7.28.2015

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
Template Structures Table
=========================

+ This panel simply displays a list of template structures which can be added to the simulation world

+ Any world structures created from a template structure will initially have all the same characteristics
	of that template
	
+ Clicking on a structure in the list selects it

======================
World Structures Table
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

< Overview Tab >

+ Structure Expense Sum Table
    After the simulation completes, the structures are sorted by how much each spent on electricity, these expense differences are the result of different storage strategies and devices usage patterns.
+ Structure Environmental Impact
    After the simulation completes, the structures are sorted by their environmental impact. The emission differences vary depending on when the structure's were buying power (peak emission times or not).
+ Time
	The total amount of time that has elapsed in the simulation since it's start.
	Time: Hours:Minutes:Seconds Date: Year/Month/Day
+ Start Date
    The date which the simulation should start from
+ End Date
    The date which the simulation should end at
+ Update Rate
	Specifies how quickly the simulation should update, for example: setting the update rate to WEEKS
	will mean the simulation will update one week per second.

< Selection Tab >

+ Displays the load profile and the devices of the world structure you have selected

< Supply and Demand Tab >

+ Supply
    Displays which power plants are producing how much power
+ Demand
    Displays the current demand in the simulation in real time as the simulation updates

< Price and Emissions Tab >

+ Price
    Displays how the price of electricity changes as demand increases
+ Emissions
    Displays how the emissions produced to meet demand change as demand increases

< Daily Trends Tab >
The date picker allows you to select any day from the simulation period and see the fluctuations in price, emissions and demand during that day.

+ Price
    Displays the changing cost of electricity ($/kWh) during the currently selected day.
+ Emissions
    Displays the changing emissions produced to meet electricity (g/kWh) demands during the currently selected day.
+ Demand
    Displays the changing demand for electricity during the currently selected day.


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


|----------------------------------|
| Creating A Functional Simulation |
|----------------------------------|

- From Predefined Template
1. There are a variety of structures that are already predefined. The structures that don't have Empty preceding their name are predefined and ready for the simulation.
    These structures don't need any devices or properties adjusted to function within the simulation.
2. Add one or more Homes to the simulation (e.g., Winter Home, Summer Home with Storage)
3. Next add one more more Power Plants to the simulation (e.g. Wind Farm, Hydro Power Plant, Gas Power Plant)
4. You'll need to specify the time range for the simulation in the Simulation Info panel (by default the start date is today's date and the end date is tomorrow's which means the simulation will run for one day)
5. You'll need to specify the update rate for the simulation in the Simulation Info panel (by default it's seconds which is quite slow)
6. You can now press Run on the tool bar and the simulation should begin

- From Scratch
1. You'll need to create a home (Empty Single Unit Building) with a least one appliance
	1.1 For that appliance(s) you'll have to get it's average consumption property (Watts need) to something other than 0
	1.2 For that appliance(s) you'll need to specify some time spans that it will be active during 
		For Example: Add Time Span and set the To value to 12:00 and leave From at 00:00 - the device will be active from midnight to noon
2. You'll need to create a power plant (Empty Power Plant)
	2.1 For that power plant you'll need to set the production cost ($ per kWh) to something other than 0
	2.3 For that power plant you'll need to set the emission rate (g per kWh) to something other than 0
3. You'll need to specify the time range for the simulation in the Simulation Info panel (by default the start date is today's date and the end date is tomorrow's which means the simulation will run for one day)
4. You'll need to specify the update rate for the simulation in the Simulation Info panel (by default it's seconds which is quite slow)
5. You can now press Run on the tool bar and the simulation should begin

