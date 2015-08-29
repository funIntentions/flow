
--[[
-- Local Average Matching Strategy:
-- This strategy first calculates the average demand during the day from the load profile of the building.
-- The storage strategies goal is for this building’s final demand profile (aggregate of the load profile and the storage profile) to remain flat and constant at this average.
-- To achieve this, the strategy will charge whenever demand is lower than the average and release energy whenever the demand is higher than the average.
--
-- input:
-- storageDevice - the storage device that is running this strategy
-- building - the building that owns the storage device that is running this strategy
-- simulationStatus - data that describes the current state of the simulation
--
-- output:
-- newStorageProfile - describes when the storage device will be storing or releasing energy and the amount of energy transferred in that moment
--]]
function strategize(storageDevice, building, simulationStatus, newStorageProfile)

    -- the building's load profile for this day of the week
    local loadProfile = building:getLoadProfilesForWeek():get(simulationStatus.dayOfTheWeek)
    -- the storage profile from the previous day
    local oldStorageProfile = simulationStatus.previousStorageProfiles:get(storageDevice:getId())
    -- kilowatts of energy that can be transfered per minute
    local transferCapacity = storageDevice:getTransferCapacity()
    -- The length needed for the storage profile. This is the number of minutes in the day -1 because the storage profile is zero indexed.
    local minutesOfDay = 1439;
    -- average demand for this building on this day of the week
    local averageDemand = 0;

    for time = 0, minutesOfDay do
        averageDemand = averageDemand + loadProfile:get(time)
    end

    averageDemand = averageDemand/loadProfile:size()

    -- for each minute of the day
    for minute = 0, minutesOfDay do

        -- demand at this time
        local demand = loadProfile:get(minute);
        -- the storage transfer amount that if added to the demand at this time would equal the average
        local requestedChargeAmount = math.floor(averageDemand - demand + 0.5)
        local transferAmount = 0;

        if requestedChargeAmount < 0 and storageDevice:getStoredEnergy() > 0 then -- release energy if possible
            -- cap the transfer amount so it won't transfer more than its transfer capacity
            if math.abs(requestedChargeAmount) < transferCapacity then
                transferAmount = requestedChargeAmount
            else
                transferAmount = -transferCapacity
            end

            -- cap the transfer amount so it won't transfer more energy than the device has stored
            if 0 > (storageDevice:getStoredEnergy() + transferAmount) then
                transferAmount = - storageDevice:getStoredEnergy()
            end
        elseif requestedChargeAmount > 0 and storageDevice:getStorageCapacity() > storageDevice:getStoredEnergy() then -- store energy if possible
            -- cap the transfer amount so it won't transfer more than its transfer capacity
            if requestedChargeAmount < transferCapacity then
                transferAmount = requestedChargeAmount
            else
                transferAmount = transferCapacity
            end

            -- cap the transfer amount so it won't transfer more energy than the device has stored
            if storageDevice:getStorageCapacity() < (storageDevice:getStoredEnergy() + transferAmount) then
                transferAmount = storageDevice:getStorageCapacity() - storageDevice:getStoredEnergy()
            end
        end

        -- update the storage device's stored energy
        storageDevice:setStoredEnergy(storageDevice:getStoredEnergy() + transferAmount)

        -- set the amount of energy to transfer during this minute of the day
        newStorageProfile:add(transferAmount)
    end
end

