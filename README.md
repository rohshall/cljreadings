cljreadings
===========

Web application in Clojure for device readings. It uses immutant for deployment, which is Clojure's interface to JBoss.

## Installation of lein-immutant

```
cat > ~/.lein/profiles.clj
{:user {:plugins [[lein-immutant "1.0.0.beta1"]]}}
```
```
lein immutant install LATEST
```

## Deployment with immutant
```
- lein immutant deploy
- lein immutant run
```
