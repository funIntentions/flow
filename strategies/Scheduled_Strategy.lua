--
-- Created by IntelliJ IDEA.
-- User: Dan
-- Date: 8/20/2015
-- Time: 12:32 PM
-- To change this template use File | Settings | File Templates.
--

function strategize(storageDevice, loadProfile, oldStorageProfile, newStorageProfile)

    local chargeTimes = {{from = 0, to = 1}, {from = 10, to = 11}, {from = 14, to = 15} }
    local dischargeTimes = {{from = 6.5, to = 7.5}, {from = 12, to = 13}, {from = 18, to = 19}}

    local transferCapacity = storageDevice:getChargingRate()/60.0
    local length = loadProfile:size() - 1;

    for time = 0, length do

        local hour = time/60.0
        local charge = false
        local discharge = false

        for item, timeSpan in pairs(chargeTimes) do
            if timeSpan.from <= hour and timeSpan.to >= hour then
                charge = true
            end
        end

        for item, timeSpan in pairs(dischargeTimes) do
            if timeSpan.from <= hour and timeSpan.to >= hour then
                discharge = true
            end
        end

        local chargeAmount = 0;

        if discharge and storageDevice:getStoredEnergy() > 0 then
            chargeAmount = -transferCapacity
            if 0 > (storageDevice:getStoredEnergy() + chargeAmount) then
                chargeAmount = - storageDevice:getStoredEnergy()
            end
        end

        if charge and storageDevice:getStorageCapacity() > storageDevice:getStoredEnergy() then
                chargeAmount = transferCapacity
            if storageDevice:getStorageCapacity() < (storageDevice:getStoredEnergy() + chargeAmount) then
                chargeAmount = storageDevice:getStorageCapacity() - storageDevice:getStoredEnergy()
            end
        end

        storageDevice:setStoredEnergy(storageDevice:getStoredEnergy() + chargeAmount)

        newStorageProfile:add(chargeAmount)
    end
end

