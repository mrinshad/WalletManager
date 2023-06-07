package controller

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	"myuniquemodulename/config"
	"myuniquemodulename/model"
	// "../model"
	// "../config"
)

// AllEmployee = Select Employee API
func AllEmployee(w http.ResponseWriter, r *http.Request) {
	var employee model.Employee
	var response model.Response
	var arrEmployee []model.Employee

	db := config.Connect()
	defer db.Close()

	rows, err := db.Query("SELECT id, name, city FROM employee")

	if err != nil {
		log.Print(err)
	}

	for rows.Next() {
		err = rows.Scan(&employee.Id, &employee.Name, &employee.City)
		if err != nil {
			log.Fatal(err.Error())
		} else {
			arrEmployee = append(arrEmployee, employee)
		}
	}

	response.Status = 200
	response.Message = "Success"
	response.Data = arrEmployee

	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")
	json.NewEncoder(w).Encode(response)
}

// InsertEmployee = Insert Employee API
func InsertEmployee(w http.ResponseWriter, r *http.Request) {
	var response model.Response

	db := config.Connect()
	defer db.Close()

	err := r.ParseMultipartForm(4096)
	if err != nil {
		panic(err)
	}
	name := r.FormValue("name")
	city := r.FormValue("city")

	_, err = db.Exec("INSERT INTO employee(name, city) VALUES(?, ?)", name, city)

	if err != nil {
		log.Print(err)
		return
	}
	response.Status = 200
	response.Message = "Insert data successfully"
	fmt.Print("Insert data to database")

	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", "*")
	json.NewEncoder(w).Encode(response)
}
