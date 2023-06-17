package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.
        User user = userRepository3.findById(userId).get();

        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        if(user == null || parkingLot == null ){
            throw new Exception("Cannot make reservation");
        }

        List<Spot> spots = parkingLot.getSpotList();

        List<Spot> spotFollow = new ArrayList<>();

        for(Spot s : spots){
            if(s.getOccupied()==false){
                int capacity;
                if(s.getSpotType()==SpotType.TWO_WHEELER){
                    capacity = 2;
                }
                else if(s.getSpotType()==SpotType.FOUR_WHEELER){
                    capacity = 4;
                }
                else{
                    capacity = Integer.MAX_VALUE;
                }
                if(capacity>numberOfWheels){
                    spotFollow.add(s);
                }
            }
        }
        if(spotFollow.isEmpty() == true){
            throw new Exception("Cannot make reservation");
        }

        Spot reserveSpot = null;
        int minimumPrice = Integer.MAX_VALUE;
        for(Spot spot : spotFollow){
            int price = spot.getPricePerHour() * timeInHours;
            if(price < minimumPrice){
                price = minimumPrice;
                reserveSpot = spot;
            }
        }
        reserveSpot.setOccupied(true);

        Reservation reservation = new Reservation();

        reservation.setSpot(reserveSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);
        reservation.setPayment(null);

        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(reserveSpot);

        return reservation;

    }
}















