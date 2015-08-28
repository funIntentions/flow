

--[[
-- Greedy Strategy:
-- Storage strategies running this strategy will either be charging or releasing electricity at all times.
-- If the storage device is empty, it will charge until it is full; conversely,
-- if the storage device is full, it will release energy until it is empty.
--
-- input:
-- storageDevice - the storage device that is running this strategy
-- building - the building that owns the storage device that is running this strategy
-- simulationStatus - data that describes the current state of the simulation
--
-- output:
-- newStorageProfile - describes when the storage device will be storing or releasing energy and the amount of energy transfer in that moment
--]]
function strategize(storageDevice, building, simulationStatus, newStorageProfile)

    -- the building's load profile for this day of the week
    local loadProfile = building:getLoadProfilesForWeek():get(simulationStatus.dayOfTheWeek)
    -- kilowatts of energy that can be transfered per minute
    local transferCapacity = storageDevice:getTransferCapacity()
    -- The length needed for the storage profile. This is the number of minutes in the day.
    local minutesOfDay = loadProfile:size() - 1
    -- possible states that a storage device running this strategy can be in
    local charging, discharging = 1, 2

    local state = charging;
    if storageDevice:getStoredEnergy() ~= storageDevice:getStorageCapacity() then
        state = discharging;
    end

    -- for each minute of the day
    for minute = 0, minutesOfDay do
        local transferAmount = 0;

        -- if the storage device is empty, start charging, else if the storage device is full, start releasing energy
        if (storageDevice:getStoredEnergy() == 0) then
            state = charging
        elseif (storageDevice:getStoredEnergy() == storageDevice:getStorageCapacity()) then
            state = discharging
        end

        -- if possible charge/discharge at the maximum capacity
        if (state == charging) then
            transferAmount = transferCapacity; -- a positive transferAmount means the storage device should store energy in this moment
            if (storageDevice:getStorageCapacity() < storageDevice:getStoredEnergy() + transferAmount) then
                transferAmount = storageDevice:getStorageCapacity() - storageDevice:getStoredEnergy()
            end
        elseif (state == discharging) then
            transferAmount = -transferCapacity -- a negative transferAmount means the storage device should release energy in this moment
            if (storageDevice:getStoredEnergy() + transferAmount < 0) then
                transferAmount = -storageDevice:getStoredEnergy()
            end
        end

        -- update the storage device's stored energy
        storageDevice:setStoredEnergy(storageDevice:getStoredEnergy() + transferAmount)

        -- set the amount of energy to transfer during this minute of the day
        newStorageProfile:add(transferAmount)
    end
end

