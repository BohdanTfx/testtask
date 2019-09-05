package com.dubrovskyi.testtask.service;

import com.dubrovskyi.testtask.domain.DirectResponse;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import gnu.trove.TDecorators;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
public class BusRouteServiceImpl implements BusRouteService {

	//Honestly, I haven't seen log message usage like this, so not sure whether it is correct or not
  private static final String           LOG_MESSAGE__NO_FILEPATH               = "No filepath specified, no routes will be initialized";
  private static final String           LOG_MESSAGE__WRONG_FILE                = "File %s not found or can not be read, no routes will be initialized";
  private static final String           LOG_MESSAGE__WRONG_FILE_FORMAT         = "File %s has wrong format. Some routes may not be initialized";
  private static final String           LOG_MESSAGE__ROUTES_INITIALIZED        = "All routes were initialized in %d ms";
  private static final String           LOG_MESSAGE__START_INITIALIZING_ROUTES = "Start initializing routes";
  private static final String           SPLIT_PATTERN__WHITESPACE              = " ";
  private static final String           EMPTY_STRING                           = "";
  private static final int              FIRST_POSITION                         = 0;
  private final Logger                  log                                    = LoggerFactory.getLogger(BusRouteServiceImpl.class);
  private SetMultimap<Integer, Integer> routemap;

  @Autowired
  private ApplicationArguments appArgs;

  @Value("${split.pattern}")
  private String splitPattern;

  @PostConstruct
  @Override
  public void initializeRoutes() {//this method is a bit overwhelmed, I would suggest make it more readable
    log.info(LOG_MESSAGE__START_INITIALIZING_ROUTES);
    long timeStart = Calendar.getInstance().getTimeInMillis();
    LocalDateTime.now().getNano(); // this new API is more flexible then Calendar/Date APIs
    routemap = createRouteMap();
    String line;
    boolean routesConsistant = true;
    if (appArgs.getSourceArgs().length == 0) {
      log.error(LOG_MESSAGE__NO_FILEPATH);//no parameter log is still acceptable
      return;
    }
    String filePath = appArgs.getSourceArgs()[FIRST_POSITION];
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      reader.readLine();
      while ((line = reader.readLine()) != null) {
        routesConsistant = fillRoute(line);
      }
    } catch (IOException e) {
      log.error(String.format(LOG_MESSAGE__WRONG_FILE, filePath));//would not recommend doing so
      log.error("File {} not found or can not be read, no routes will be initialized", filePath);/*this is more natural usage, 
      since under the hood there is a logic to check where specific log level is available*/ 
    }
    if (!routesConsistant) {
      log.error(LOG_MESSAGE__WRONG_FILE_FORMAT, filePath);
    } else {
      log.info(String.format(LOG_MESSAGE__ROUTES_INITIALIZED, (Calendar.getInstance().getTimeInMillis() - timeStart)));
    }
  }

  @Override
  public DirectResponse isDirect(int depSid, int arrSid) {//if u r Lambda-Jedi u can compress this method into 1 line)
    Set<Integer> departureRoutes = routemap.get(depSid);
    Set<Integer> arrivalRoutes = routemap.get(arrSid);
    departureRoutes.retainAll(arrivalRoutes);
    return new DirectResponse(depSid, arrSid, !departureRoutes.isEmpty());
  }

  private SetMultimap<Integer,Integer> createRouteMap() {
    return Multimaps.newSetMultimap(
        TDecorators.wrap(new TIntObjectHashMap<>()),
        () -> TDecorators.wrap(new TIntHashSet()));
  }

  private boolean fillRoute(String line) {
    if (splitPattern == null) splitPattern = SPLIT_PATTERN__WHITESPACE;//bad practice to use if without {}
    Iterable<String> strings = Splitter.on(splitPattern).split(line);

    try {
      int routeId = Integer.parseInt(strings.iterator().next());

      StreamSupport.stream(strings.spliterator(), false)
        .skip(1)
        .filter(s -> !EMPTY_STRING.equals(s) )
//        .filter(s -> !s.isEmpty()) - empty-check already present, NPE check should be considered I guess
        .map(Integer::parseInt)
        .forEach(x -> routemap.put(x, routeId));
    }
    catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  //---------------Just for testing purpouses------------------------------
  protected SetMultimap<Integer, Integer> getRoutemap() {
    return routemap;
  }
  protected void setTestMap(SetMultimap<Integer, Integer> routemap) {
    this.routemap = routemap;
  }
}
