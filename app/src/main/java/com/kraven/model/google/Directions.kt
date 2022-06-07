package com.kraven.delivery.model.google

import com.google.gson.annotations.SerializedName

class Directions {

    /**
     * geocoded_waypoints : [{"geocoder_status":"OK","place_id":"ChIJ-Y25iEuDXjkRCdnta-6NYxs","types":["street_address"]},{"geocoder_status":"OK","place_id":"ChIJLYLNtlObXjkRqyAEZlW_ujA","types":["route"]}]
     * routes : [{"bounds":{"northeast":{"lat":23.0751647,"lng":72.5251301},"southwest":{"lat":23.0462178,"lng":72.5154725}},"copyrights":"Map data ©2018 Google","legs":[{"distance":{"text":"3.7 km","value":3733},"duration":{"text":"9 mins","value":514},"end_address":"Sarkhej - Gandhinagar Hwy, Bodakdev, Ahmedabad, Gujarat 380054, India","end_location":{"lat":23.0488473,"lng":72.5166279},"start_address":"312, Sarkhej - Gandhinagar Hwy, Sola, Ahmedabad, Gujarat 380060, India","start_location":{"lat":23.0751455,"lng":72.5251301},"steps":[{"distance":{"text":"0.1 km","value":145},"duration":{"text":"1 min","value":42},"end_location":{"lat":23.0739826,"lng":72.5247022},"html_instructions":"Head **south<\/b><div style=\"font-size:0.9em\">Partial restricted usage road<\/div>","polyline":{"points":"uzykCaatyLANbCf@fB\\"},"start_location":{"lat":23.0751455,"lng":72.5251301},"travel_mode":"DRIVING"},{"distance":{"text":"13 m","value":13},"duration":{"text":"1 min","value":2},"end_location":{"lat":23.0740092,"lng":72.5245735},"html_instructions":"Turn **right<\/b> toward **Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-right","polyline":{"points":"ksykCk~syLEX"},"start_location":{"lat":23.0739826,"lng":72.5247022},"travel_mode":"DRIVING"},{"distance":{"text":"2.1 km","value":2098},"duration":{"text":"4 mins","value":232},"end_location":{"lat":23.0557744,"lng":72.519324},"html_instructions":"Turn **left<\/b> onto **Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-left","polyline":{"points":"qsykCq}syLr@L|Cl@hCd@xAZtB`@nCf@fDp@h@Jj@JtBb@bBXrB^t@NtDt@hARfARdJhBlB^nATrDt@tEz@p@LbDn@fDh@dB\\TDzHbBtEx@"},"start_location":{"lat":23.0740092,"lng":72.5245735},"travel_mode":"DRIVING"},{"distance":{"text":"1.1 km","value":1127},"duration":{"text":"3 mins","value":171},"end_location":{"lat":23.0462909,"lng":72.5159134},"html_instructions":"Continue straight","maneuver":"straight","polyline":{"points":"qavkCw|ryLDYND~QnDhATpDz@bD~@tA^f@HRF^LJD`H`CpJrD"},"start_location":{"lat":23.0557744,"lng":72.519324},"travel_mode":"DRIVING"},{"distance":{"text":"56 m","value":56},"duration":{"text":"1 min","value":24},"end_location":{"lat":23.0464249,"lng":72.5154725},"html_instructions":"Turn **right<\/b> onto **Sargam Marg<\/b>","maneuver":"turn-right","polyline":{"points":"iftkCmgryLLDELIRQ`@CFAD"},"start_location":{"lat":23.0462909,"lng":72.5159134},"travel_mode":"DRIVING"},{"distance":{"text":"0.3 km","value":294},"duration":{"text":"1 min","value":43},"end_location":{"lat":23.0488473,"lng":72.5166279},"html_instructions":"Turn **right<\/b> at **Patel Ave<\/b><div style=\"font-size:0.9em\">Pass by Landmark Honda (on the left)<\/div>","maneuver":"turn-right","polyline":{"points":"cgtkCudryLMGqBy@SImBs@eC}@}Ai@"},"start_location":{"lat":23.0464249,"lng":72.5154725},"travel_mode":"DRIVING"}],"traffic_speed_entry":[],"via_waypoint":[]}],"overview_polyline":{"points":"uzykCaatyLANbCf@fB\\EXpEz@bF`AvMfC`Dn@vEx@jFdApCf@vUtEfGhAbDn@fDh@zBb@zHbBtEx@DYNDhTdEpDz@bD~@|Bh@`J|C~JxDO`@Uh@ADMGeCcAsFqB}Ai@"},"summary":"Sarkhej - Gandhinagar Hwy","warnings":[],"waypoint_order":[]}]
     * status : OK
     * error_message : Invalid request. Missing the 'destination' parameter.
    </div>****************</div>** */

    @SerializedName("status")
    var status: String? = null
    @SerializedName("error_message")
    var errorMessage: String? = null
    @SerializedName("geocoded_waypoints")
    var geocodedWaypoints: List<GeocodedWaypoints>? = null
    @SerializedName("routes")
    var routes: List<Routes>? = null

    class GeocodedWaypoints {
        /**
         * geocoder_status : OK
         * place_id : ChIJ-Y25iEuDXjkRCdnta-6NYxs
         * types : ["street_address"]
         */

        @SerializedName("geocoder_status")
        var geocoderStatus: String? = null
        @SerializedName("place_id")
        var placeId: String? = null
        @SerializedName("types")
        var types: List<String>? = null
    }

    class Routes {
        /**
         * bounds : {"northeast":{"lat":23.0751647,"lng":72.5251301},"southwest":{"lat":23.0462178,"lng":72.5154725}}
         * copyrights : Map data ©2018 Google
         * legs : [{"distance":{"text":"3.7 km","value":3733},"duration":{"text":"9 mins","value":514},"end_address":"Sarkhej - Gandhinagar Hwy, Bodakdev, Ahmedabad, Gujarat 380054, India","end_location":{"lat":23.0488473,"lng":72.5166279},"start_address":"312, Sarkhej - Gandhinagar Hwy, Sola, Ahmedabad, Gujarat 380060, India","start_location":{"lat":23.0751455,"lng":72.5251301},"steps":[{"distance":{"text":"0.1 km","value":145},"duration":{"text":"1 min","value":42},"end_location":{"lat":23.0739826,"lng":72.5247022},"html_instructions":"Head **south<\/b><div style=\"font-size:0.9em\">Partial restricted usage road<\/div>","polyline":{"points":"uzykCaatyLANbCf@fB\\"},"start_location":{"lat":23.0751455,"lng":72.5251301},"travel_mode":"DRIVING"},{"distance":{"text":"13 m","value":13},"duration":{"text":"1 min","value":2},"end_location":{"lat":23.0740092,"lng":72.5245735},"html_instructions":"Turn **right<\/b> toward **Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-right","polyline":{"points":"ksykCk~syLEX"},"start_location":{"lat":23.0739826,"lng":72.5247022},"travel_mode":"DRIVING"},{"distance":{"text":"2.1 km","value":2098},"duration":{"text":"4 mins","value":232},"end_location":{"lat":23.0557744,"lng":72.519324},"html_instructions":"Turn **left<\/b> onto **Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-left","polyline":{"points":"qsykCq}syLr@L|Cl@hCd@xAZtB`@nCf@fDp@h@Jj@JtBb@bBXrB^t@NtDt@hARfARdJhBlB^nATrDt@tEz@p@LbDn@fDh@dB\\TDzHbBtEx@"},"start_location":{"lat":23.0740092,"lng":72.5245735},"travel_mode":"DRIVING"},{"distance":{"text":"1.1 km","value":1127},"duration":{"text":"3 mins","value":171},"end_location":{"lat":23.0462909,"lng":72.5159134},"html_instructions":"Continue straight","maneuver":"straight","polyline":{"points":"qavkCw|ryLDYND~QnDhATpDz@bD~@tA^f@HRF^LJD`H`CpJrD"},"start_location":{"lat":23.0557744,"lng":72.519324},"travel_mode":"DRIVING"},{"distance":{"text":"56 m","value":56},"duration":{"text":"1 min","value":24},"end_location":{"lat":23.0464249,"lng":72.5154725},"html_instructions":"Turn **right<\/b> onto **Sargam Marg<\/b>","maneuver":"turn-right","polyline":{"points":"iftkCmgryLLDELIRQ`@CFAD"},"start_location":{"lat":23.0462909,"lng":72.5159134},"travel_mode":"DRIVING"},{"distance":{"text":"0.3 km","value":294},"duration":{"text":"1 min","value":43},"end_location":{"lat":23.0488473,"lng":72.5166279},"html_instructions":"Turn **right<\/b> at **Patel Ave<\/b><div style=\"font-size:0.9em\">Pass by Landmark Honda (on the left)<\/div>","maneuver":"turn-right","polyline":{"points":"cgtkCudryLMGqBy@SImBs@eC}@}Ai@"},"start_location":{"lat":23.0464249,"lng":72.5154725},"travel_mode":"DRIVING"}],"traffic_speed_entry":[],"via_waypoint":[]}]
         * overview_polyline : {"points":"uzykCaatyLANbCf@fB\\EXpEz@bF`AvMfC`Dn@vEx@jFdApCf@vUtEfGhAbDn@fDh@zBb@zHbBtEx@DYNDhTdEpDz@bD~@|Bh@`J|C~JxDO`@Uh@ADMGeCcAsFqB}Ai@"}
         * summary : Sarkhej - Gandhinagar Hwy
         * warnings : []
         * waypoint_order : []
        </div>****************</div>** */

        @SerializedName("bounds")
        var bounds: Bounds? = null
        @SerializedName("copyrights")
        var copyrights: String? = null
        @SerializedName("overview_polyline")
        var overviewPolyline: OverviewPolyline? = null
        @SerializedName("summary")
        var summary: String? = null
        @SerializedName("legs")
        var legs: List<Legs>? = null
        @SerializedName("warnings")
        var warnings: List<String>? = null
        @SerializedName("waypoint_order")
        var waypointOrder: List<String>? = null

        class Bounds {
            /**
             * northeast : {"lat":23.0751647,"lng":72.5251301}
             * southwest : {"lat":23.0462178,"lng":72.5154725}
             */

            @SerializedName("northeast")
            var northeast: Northeast? = null
            @SerializedName("southwest")
            var southwest: Southwest? = null

            class Northeast {
                /**
                 * lat : 23.0751647
                 * lng : 72.5251301
                 */

                @SerializedName("lat")
                var lat: Double = 0.toDouble()
                @SerializedName("lng")
                var lng: Double = 0.toDouble()
            }

            class Southwest {
                /**
                 * lat : 23.0462178
                 * lng : 72.5154725
                 */

                @SerializedName("lat")
                var lat: Double = 0.toDouble()
                @SerializedName("lng")
                var lng: Double = 0.toDouble()
            }
        }

        class OverviewPolyline {
            /**
             * points : uzykCaatyLANbCf@fB\EXpEz@bF`AvMfC`Dn@vEx@jFdApCf@vUtEfGhAbDn@fDh@zBb@zHbBtEx@DYNDhTdEpDz@bD~@|Bh@`J|C~JxDO`@Uh@ADMGeCcAsFqB}Ai@
             */

            @SerializedName("points")
            var points: String? = null
        }

        class Legs {
            /**
             * distance : {"text":"3.7 km","value":3733}
             * duration : {"text":"9 mins","value":514}
             * end_address : Sarkhej - Gandhinagar Hwy, Bodakdev, Ahmedabad, Gujarat 380054, India
             * end_location : {"lat":23.0488473,"lng":72.5166279}
             * start_address : 312, Sarkhej - Gandhinagar Hwy, Sola, Ahmedabad, Gujarat 380060, India
             * start_location : {"lat":23.0751455,"lng":72.5251301}
             * steps : [{"distance":{"text":"0.1 km","value":145},"duration":{"text":"1 min","value":42},"end_location":{"lat":23.0739826,"lng":72.5247022},"html_instructions":"Head **south<\/b><div style=\"font-size:0.9em\">Partial restricted usage road<\/div>","polyline":{"points":"uzykCaatyLANbCf@fB\\"},"start_location":{"lat":23.0751455,"lng":72.5251301},"travel_mode":"DRIVING"},{"distance":{"text":"13 m","value":13},"duration":{"text":"1 min","value":2},"end_location":{"lat":23.0740092,"lng":72.5245735},"html_instructions":"Turn **right<\/b> toward **Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-right","polyline":{"points":"ksykCk~syLEX"},"start_location":{"lat":23.0739826,"lng":72.5247022},"travel_mode":"DRIVING"},{"distance":{"text":"2.1 km","value":2098},"duration":{"text":"4 mins","value":232},"end_location":{"lat":23.0557744,"lng":72.519324},"html_instructions":"Turn **left<\/b> onto **Sarkhej - Gandhinagar Hwy<\/b>","maneuver":"turn-left","polyline":{"points":"qsykCq}syLr@L|Cl@hCd@xAZtB`@nCf@fDp@h@Jj@JtBb@bBXrB^t@NtDt@hARfARdJhBlB^nATrDt@tEz@p@LbDn@fDh@dB\\TDzHbBtEx@"},"start_location":{"lat":23.0740092,"lng":72.5245735},"travel_mode":"DRIVING"},{"distance":{"text":"1.1 km","value":1127},"duration":{"text":"3 mins","value":171},"end_location":{"lat":23.0462909,"lng":72.5159134},"html_instructions":"Continue straight","maneuver":"straight","polyline":{"points":"qavkCw|ryLDYND~QnDhATpDz@bD~@tA^f@HRF^LJD`H`CpJrD"},"start_location":{"lat":23.0557744,"lng":72.519324},"travel_mode":"DRIVING"},{"distance":{"text":"56 m","value":56},"duration":{"text":"1 min","value":24},"end_location":{"lat":23.0464249,"lng":72.5154725},"html_instructions":"Turn **right<\/b> onto **Sargam Marg<\/b>","maneuver":"turn-right","polyline":{"points":"iftkCmgryLLDELIRQ`@CFAD"},"start_location":{"lat":23.0462909,"lng":72.5159134},"travel_mode":"DRIVING"},{"distance":{"text":"0.3 km","value":294},"duration":{"text":"1 min","value":43},"end_location":{"lat":23.0488473,"lng":72.5166279},"html_instructions":"Turn **right<\/b> at **Patel Ave<\/b><div style=\"font-size:0.9em\">Pass by Landmark Honda (on the left)<\/div>","maneuver":"turn-right","polyline":{"points":"cgtkCudryLMGqBy@SImBs@eC}@}Ai@"},"start_location":{"lat":23.0464249,"lng":72.5154725},"travel_mode":"DRIVING"}]
             * traffic_speed_entry : []
             * via_waypoint : []
            </div>****************</div>** */

            @SerializedName("distance")
            var distance: Distance? = null
            @SerializedName("duration")
            var duration: Duration? = null
            @SerializedName("end_address")
            var endAddress: String? = null
            @SerializedName("end_location")
            var endLocation: EndLocation? = null
            @SerializedName("start_address")
            var startAddress: String? = null
            @SerializedName("start_location")
            var startLocation: StartLocation? = null
            @SerializedName("steps")
            var steps: List<Steps>? = null
            @SerializedName("traffic_speed_entry")
            var trafficSpeedEntry: List<String>? = null
            @SerializedName("via_waypoint")
            var viaWaypoint: List<String>? = null

            class Distance {
                /**
                 * text : 3.7 km
                 * value : 3733
                 */

                @SerializedName("text")
                var text: String? = null
                @SerializedName("value")
                var value: Int = 0
            }

            class Duration {
                /**
                 * text : 9 mins
                 * value : 514
                 */

                @SerializedName("text")
                var text: String? = null
                @SerializedName("value")
                var value: Int = 0
            }

            class EndLocation {
                /**
                 * lat : 23.0488473
                 * lng : 72.5166279
                 */

                @SerializedName("lat")
                var lat: Double = 0.toDouble()
                @SerializedName("lng")
                var lng: Double = 0.toDouble()
            }

            class StartLocation {
                /**
                 * lat : 23.0751455
                 * lng : 72.5251301
                 */

                @SerializedName("lat")
                var lat: Double = 0.toDouble()
                @SerializedName("lng")
                var lng: Double = 0.toDouble()
            }

            class Steps {
                /**
                 * distance : {"text":"0.1 km","value":145}
                 * duration : {"text":"1 min","value":42}
                 * end_location : {"lat":23.0739826,"lng":72.5247022}
                 * html_instructions : Head **south**<div style="font-size:0.9em">Partial restricted usage road</div>
                 * polyline : {"points":"uzykCaatyLANbCf@fB\\"}
                 * start_location : {"lat":23.0751455,"lng":72.5251301}
                 * travel_mode : DRIVING
                 * maneuver : turn-right
                 */

                @SerializedName("distance")
                var distance: DistanceX? = null
                @SerializedName("duration")
                var duration: DurationX? = null
                @SerializedName("end_location")
                var endLocation: EndLocationX? = null
                @SerializedName("html_instructions")
                var htmlInstructions: String? = null
                @SerializedName("polyline")
                var polyline: Polyline? = null
                @SerializedName("start_location")
                var startLocation: StartLocationX? = null
                @SerializedName("travel_mode")
                var travelMode: String? = null
                @SerializedName("maneuver")
                var maneuver: String? = null

                class DistanceX {
                    /**
                     * text : 0.1 km
                     * value : 145
                     */

                    @SerializedName("text")
                    var text: String? = null
                    @SerializedName("value")
                    var value: Int = 0
                }

                class DurationX {
                    /**
                     * text : 1 min
                     * value : 42
                     */

                    @SerializedName("text")
                    var text: String? = null
                    @SerializedName("value")
                    var value: Int = 0
                }

                class EndLocationX {
                    /**
                     * lat : 23.0739826
                     * lng : 72.5247022
                     */

                    @SerializedName("lat")
                    var lat: Double = 0.toDouble()
                    @SerializedName("lng")
                    var lng: Double = 0.toDouble()
                }

                class Polyline {
                    /**
                     * points : uzykCaatyLANbCf@fB\
                     */

                    @SerializedName("points")
                    var points: String? = null
                }

                class StartLocationX {
                    /**
                     * lat : 23.0751455
                     * lng : 72.5251301
                     */

                    @SerializedName("lat")
                    var lat: Double = 0.toDouble()
                    @SerializedName("lng")
                    var lng: Double = 0.toDouble()
                }
            }
        }
    }
}
