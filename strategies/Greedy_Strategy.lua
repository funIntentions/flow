--
-- Created by IntelliJ IDEA.
-- User: Dan
-- Date: 8/6/2015
-- Time: 10:43 AM
-- To change this template use File | Settings | File Templates.
--

function strategize(storageDevice, building, simulationStatus, newStorageProfile)

    local loadProfile = building:getLoadProfilesForWeek():get(simulationStatus.dayOfTheWeek)
    local transferCapacity = storageDevice:getTransferCapacity()
    local length = loadProfile:size() - 1;
    local charging, discharging = 1, 2
    local state = charging;

    if storageDevice:getStoredEnergy() ~= storageDevice:getStorageCapacity() then
        state = discharging;
    end

    for time = 0, length do
        local chargeAmount = 0;

        if (storageDevice:getStoredEnergy() == 0) then
            state = charging
        elseif (storageDevice:getStoredEnergy() == storageDevice:getStorageCapacity()) then
            state = discharging
        end

        if (state == charging) then
            chargeAmount = transferCapacity;
            if (storageDevice:getStorageCapacity() < storageDevice:getStoredEnergy() + chargeAmount) then
                chargeAmount = storageDevice:getStorageCapacity() - storageDevice:getStoredEnergy()
            end
        elseif (state == discharging) then
            chargeAmount = -transferCapacity
            if (storageDevice:getStoredEnergy() + chargeAmount < 0) then
                chargeAmount = -storageDevice:getStoredEnergy()
            end
        end

        storageDevice:setStoredEnergy(storageDevice:getStoredEnergy() + chargeAmount)

        newStorageProfile:add(chargeAmount)
    end
end

