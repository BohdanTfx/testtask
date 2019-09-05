package com.dubrovskyi.testtask.web;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.dubrovskyi.testtask.service.BusRouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BusRouteController {

  private final BusRouteService busRouteService;

  public BusRouteController(BusRouteService busRouteService) {//again, consider lombok usage
    this.busRouteService = busRouteService;
  }

  @GetMapping("/direct")
  public ResponseEntity<DirectResponse> direct(@RequestParam(name = "dep_sid") int depSid,//Personally, I'm not primitive fan, so dont really like it here
                                               @RequestParam(name = "arr_sid") int arrSid ) {//in my opinion parameters must be more self-descriptive
//would suggest logs on controller level
    DirectResponse direct = busRouteService.isDirect(depSid, arrSid);
    return new ResponseEntity<>(direct, HttpStatus.OK);
//    return ResponseEntity.ok(direct); - there are few useful shortcuts
  }
}
