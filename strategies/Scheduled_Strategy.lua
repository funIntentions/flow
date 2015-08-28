--[[
-- Scheduled Strategy:
-- This strategy has a series of time spans when it will charge energy and a series of time spans when it will release energy.
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
    -- the time spans where the storage strategy will try and store energy
    local chargeTimes = {{from = 0, to = 1}, {from = 10, to = 11}, {from = 14, to = 15} }
    -- the time spans where the storage strategy will try and release energy
    local dischargeTimes = {{from = 6.5, to = 7.5}, {from = 12, to = 13}, {from = 18, to = 19}}
    -- kilowatts of energy that can be transfered per minute
    local transferCapacity = storageDevice:getTransferCapacity()
    -- The length needed for the storage profile. This is the number of minutes in the day -1 because the storage profile is zero indexed.
    local minutesOfDay = 1439

    -- for each minute of the day
    for minute = 0, minutesOfDay do

        local hour = minute /60.0
        local charge = false
        local discharge = false

        -- checks if during this hour the strategy should try and store energy
        for item, timeSpan in pairs(chargeTimes) do
            if timeSpan.from <= hour and timeSpan.to >= hour then
                charge = true
            end
        end

        -- checks if during this hour the strategy should try and release energy
        for item, timeSpan in pairs(dischargeTimes) do
            if timeSpan.from <= hour and timeSpan.to >= hour then
                discharge = true
            end
        end

        local transferAmount = 0;

        -- checks if there is stored energy to release
        if discharge and storageDevice:getStoredEnergy() > 0 then
            transferAmount = -transferCapacity
            -- caps the transfer amount so more energy isn't release than there is stored in the device
            if 0 > (storageDevice:getStoredEnergy() + transferAmount) then
                transferAmount = - storageDevice:getStoredEnergy()
            end
        end

        -- checks if there is room to store more energy in the storage device
        if charge and storageDevice:getStorageCapacity() > storageDevice:getStoredEnergy() then
            transferAmount = transferCapacity
            -- caps the transfer amount so more energy isn't stored than there is room in the device
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

