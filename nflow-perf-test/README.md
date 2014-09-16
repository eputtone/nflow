gatling usage
===============


mvn gatling:execute -Dgatling.simulationClass=com.nitorcreations.nflow.perf.BasicSimulation



Test plan
=========
- two executors
-- nflow jettys

- use metrics listener

- use gatling tool to generate load
-- some standard load N new jobs per second
-- some spikes K jobs as fast as possible
-- keep a list of outstanding jobs
-- poll periodically for jobs, remove from list when done
-- at end poll for remaining jobs (or timeout)

- workflows
-- make 5 separate workflows
--- some workflows are fast: 5 - 1000 msec / state
--- some workflows slow: 1 sec - 20 sec / state
-- complex, about 10 states
-- use Thread.sleep as body

-- some of them generate retries

