package com.arcadia.operations.api.service;

import com.arcadia.operations.api.domain.VehicleTrip;
import com.arcadia.operations.api.dto.CreateTripRequest;
import com.arcadia.operations.api.dto.TripResponse;
import com.arcadia.operations.api.repository.VehicleTripRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class VehicleTripServiceImpl implements VehicleTripService {

    private final VehicleTripRepository repository;

    public VehicleTripServiceImpl(VehicleTripRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TripResponse createTrip(String vinValue, CreateTripRequest request) {

        String vin = normalizeVin(vinValue);

        if (request == null ||
                request.engineStartTime() == null ||
                request.engineEndTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trip details and engine timestamps are required"
            );
        }

        if (!request.engineEndTime().isAfter(request.engineStartTime())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Engine end time must be later than engine start time"
            );
        }

        if (request.kmDriven() == null || request.kmDriven().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "KM driven must be zero or greater"
            );
        }

        VehicleTrip trip = VehicleTrip.create();

        trip.setVin(vin);
        trip.setStartAddress(request.startAddress().trim());
        trip.setEndAddress(request.endAddress().trim());
        trip.setEngineStartTime(request.engineStartTime());
        trip.setEngineEndTime(request.engineEndTime());
        trip.setKmDriven(request.kmDriven());

        VehicleTrip savedTrip = repository.save(trip);

        return toResponse(savedTrip);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> getTrips(String vinValue) {

        String vin = normalizeVin(vinValue);

        return repository
                .findAllByVinOrderByEngineStartTimeDesc(vin)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTrip(String vinValue, UUID tripId) {

        String vin = normalizeVin(vinValue);

        VehicleTrip trip = repository.findByIdAndVin(tripId, vin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found for VIN: " + vin
                ));

        return toResponse(trip);
    }

    private TripResponse toResponse(VehicleTrip trip) {

        return new TripResponse(
                trip.getId(),
                trip.getVin(),
                trip.getStartAddress(),
                trip.getEndAddress(),
                trip.getEngineStartTime(),
                trip.getEngineEndTime(),
                trip.getKmDriven(),
                trip.getCreatedAt()
        );
    }

    private String normalizeVin(String vin) {

        if (vin == null || vin.isBlank() || vin.trim().length() > 17) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid VIN"
            );
        }

        return vin.trim().toUpperCase(Locale.ROOT);
    }
}