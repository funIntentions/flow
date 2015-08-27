--
-- Created by IntelliJ IDEA.
-- User: Dan
-- Date: 8/5/2015
-- Time: 6:36 PM
-- To change this template use File | Settings | File Templates.
--

function strategize(storageDevice, loadProfile, oldStorageProfile, newStorageProfile)

    local transferCapacity = storageDevice:getChargingRate()
    local averageDemand = 0;
    local length = loadProfile:size() - 1;

    for time = 0, length do
        averageDemand = averageDemand + loadProfile:get(time)
    end

    averageDemand = averageDemand/loadProfile:size()

    for time = 0, length do

        local demand = loadProfile:get(time);
        local requestedChargeAmount = math.floor(averageDemand - demand + 0.5)
        local chargeAmount = 0;

        if requestedChargeAmount < 0 and storageDevice:getStoredEnergy() > 0 then
            if math.abs(requestedChargeAmount) < transferCapacity then
                chargeAmount = requestedChargeAmount
            else
                chargeAmount = -transferCapacity
            end

            if 0 > (storageDevice:getStoredEnergy() + chargeAmount) then
                chargeAmount = - storageDevice:getStoredEnergy()
            end
        elseif requestedChargeAmount > 0 and storageDevice:getStorageCapacity() > storageDevice:getStoredEnergy() then
            if requestedChargeAmount < transferCapacity then
                chargeAmount = requestedChargeAmount
            else
                chargeAmount = transferCapacity
            end

            if storageDevice:getStorageCapacity() < (storageDevice:getStoredEnergy() + chargeAmount) then
                chargeAmount = storageDevice:getStorageCapacity() - storageDevice:getStoredEnergy()
            end
        else
            chargeAmount = oldStorageProfile:get(time)
        end

        storageDevice:setStoredEnergy(storageDevice:getStoredEnergy() + chargeAmount)

        newStorageProfile:add(chargeAmount)
    end
end

