package com.ll.hereispaw.domain.missing.missing.repository;

import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissingRepository extends JpaRepository<Missing, Long> {
    @Query(value = "SELECT * FROM missing m WHERE ST_DWithin(" +
            "ST_SetSRID(ST_MakePoint(CAST(ST_X(ST_GeomFromText(m.geo)) AS float), CAST(ST_Y(ST_GeomFromText(m.geo)) AS float)), 4326)::geography, " +
            "ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, " +
            ":radius)", nativeQuery = true)
    List<Missing> findWithinRadius(@Param("latitude") Double latitude,
                                   @Param("longitude") Double longitude,
                                   @Param("radius") Double radius);

}
