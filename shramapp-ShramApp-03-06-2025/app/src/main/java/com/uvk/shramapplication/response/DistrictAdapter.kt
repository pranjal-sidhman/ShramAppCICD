
package com.uvk.shramapplication.response

/*
class DistrictAdapter(
    private var cityList: ArrayList<DistrictData>,
    private val onCityChecked: (DistrictData, Boolean) -> Unit
) :
    RecyclerView.Adapter<DistrictAdapter.CityViewHolder>() {
    fun setData(newCityList: List<DistrictData>) {
        cityList.clear()
        cityList.addAll(newCityList)
        notifyDataSetChanged()
    }

    var cityLists = ArrayList<DistrictData>()
    fun setData1(newCityList: List<DistrictData>, data: DATA) {
        cityList.clear()

        Log.e("TAG", "CITY_IDcheck1: " + data.CITY_ID)
        Log.e("TAG", "CITY_IDcheck2: " + data.CITY_ID_2)
        Log.e("TAG", "CITY_IDcheck3: " + data.CITY_ID_3)
        Log.e("TAG", "CITY_IDcheck4: " + data.CITY_ID_4)
        Log.e("TAG", "STATE_ID: " + data.STATE_ID)
        for (i in newCityList) {
            Log.e("TAG", "CITY_IDsetDatsada1: " + i.id)
          //  Log.e("TAG", "CITY_IDsetDatsada2: " + i.STATE_ID)
            Log.e("TAG", "CITY_IDsetDatsada3: " + i.district_name)


            if (i.id == data.CITY_ID) {
                Log.e("TAG", "CITY_IDsetDatsada4: " + i.CITY_ID  + data.CITY_ID)
                cityList.add(CityData(data.CITY_ID, i.CITY_NAME, i.STATE_ID, true))
                cityLists.add(CityData(data.CITY_ID, i.CITY_NAME, i.STATE_ID, true))
            } else
                if (data.CITY_ID_2 == i.CITY_ID) {
                    Log.e("TAG", "CITY_IDsetDatsada5: " + i.CITY_ID  + data.CITY_ID)
                    cityList.add(CityData(data.CITY_ID_2, i.CITY_NAME, i.STATE_ID, true))
                    cityLists.add(CityData(data.CITY_ID_2, i.CITY_NAME, i.STATE_ID, true))
                } else if (data.CITY_ID_3 == i.CITY_ID) {
                    Log.e("TAG", "CITY_IDsetDatsada6: " + i.CITY_ID  + data.CITY_ID)
                    cityList.add(CityData(data.CITY_ID_3, i.CITY_NAME, i.STATE_ID, true))
                    cityLists.add(CityData(data.CITY_ID_3, i.CITY_NAME, i.STATE_ID, true))
                } else if (data.CITY_ID_4 == i.CITY_ID) {
                    Log.e("TAG", "CITY_IDsetDatsada7: " + i.CITY_ID  + data.CITY_ID)
                    cityList.add(CityData(data.CITY_ID_4, i.CITY_NAME, i.STATE_ID, true))
                    cityLists.add(CityData(data.CITY_ID_4, i.CITY_NAME, i.STATE_ID, true))
                } else

                    if (data.CITY_ID_5 == i.CITY_ID) {
                        Log.e("TAG", "CITY_IDsetDatsada8: " + i.CITY_ID  + data.CITY_ID)
                        cityList.add(CityData(data.CITY_ID_5, i.CITY_NAME, i.STATE_ID, true))
                        cityLists.add(CityData(data.CITY_ID_5, i.CITY_NAME, i.STATE_ID, true))
                    } else

                        if (data.CITY_ID_6 == i.CITY_ID) {
                            Log.e("TAG", "CITY_IDsetDatsada9: " + i.CITY_ID  + data.CITY_ID)
                            cityList.add(CityData(data.CITY_ID_6, i.CITY_NAME, i.STATE_ID, true))
                            cityLists.add(CityData(data.CITY_ID_6, i.CITY_NAME, i.STATE_ID, true))
                        } else

                            if (data.CITY_ID_7 == i.CITY_ID) {
                                Log.e("TAG", "CITY_IDsetDatsada10: " + i.CITY_ID  + data.CITY_ID)
                                cityList.add(
                                    CityData(
                                        data.CITY_ID_7,
                                        i.CITY_NAME,
                                        i.STATE_ID,
                                        true
                                    )
                                )
                                cityLists.add(
                                    CityData(
                                        data.CITY_ID_7,
                                        i.CITY_NAME,
                                        i.STATE_ID,
                                        true
                                    )
                                )
                            } else
                                if (data.CITY_ID_8 == i.CITY_ID) {
                                    Log.e("TAG", "CITY_IDsetDatsada11: " + i.CITY_ID  + data.CITY_ID)
                                    cityList.add(
                                        CityData(
                                            data.CITY_ID_8,
                                            i.CITY_NAME,
                                            i.STATE_ID,
                                            true
                                        )
                                    )
                                    cityLists.add(
                                        CityData(
                                            data.CITY_ID_8,
                                            i.CITY_NAME,
                                            i.STATE_ID,
                                            true
                                        )
                                    )
                                } else
                                    if (data.CITY_ID_9 == i.CITY_ID) {
                                        Log.e("TAG", "CITY_IDsetDatsada12: " + i.CITY_ID  + data.CITY_ID)
                                        cityList.add(
                                            CityData(
                                                data.CITY_ID_9,
                                                i.CITY_NAME,
                                                i.STATE_ID,
                                                true
                                            )
                                        )
                                        cityLists.add(
                                            CityData(
                                                data.CITY_ID_9,
                                                i.CITY_NAME,
                                                i.STATE_ID,
                                                true
                                            )
                                        )
                                    } else
                                        if (data.CITY_ID_10 == i.CITY_ID) {
                                            Log.e("TAG", "CITY_IDsetDatsada13: " + i.CITY_ID  + data.CITY_ID)
                                            cityList.add(
                                                CityData(
                                                    data.CITY_ID_10,
                                                    i.CITY_NAME,
                                                    i.STATE_ID,
                                                    true
                                                )
                                            )
                                            cityLists.add(
                                                CityData(
                                                    data.CITY_ID_10,
                                                    i.CITY_NAME,
                                                    i.STATE_ID,
                                                    true
                                                )
                                            )
                                        } else {
                                            Log.e("TAG", "CITY_IDsetDatsada14: " + i.CITY_ID  +"  "+ data.CITY_ID)
                                            cityList.add(
                                                CityData(
                                                    i.CITY_ID,
                                                    i.CITY_NAME,
                                                    i.STATE_ID,
                                                    i.isSelected
                                                )
                                            )


                                        }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val city = cityList[position]
        holder.bind(city)
    }

    override fun getItemCount(): Int {
        return cityList.size
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkboxCity)

        fun bind(city: DistrictData) {
            checkBox.text = city.district_name
            checkBox.isChecked = city.isSelected


            val idExists = cityLists.any { it.id == city.id }
//            checkBox.isEnabled = !idExists
            checkBox.setOnCheckedChangeListener { _, isChecked ->

                city.isSelected = isChecked
                onCityChecked(city, isChecked)

            }


        }
    }
}
*/
