import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class Errors {

    constructor() { }

    public errorStatus406(json_data:any){

        var full_string="";
          Object.keys(json_data).forEach(element => {
            full_string += element+" : " + json_data[element]+"\n";
          });

        return full_string;
    }

    public directDisplayErrorMessageStatus406(json_data:any){

        var full_string="";
          Object.keys(json_data).forEach(element => {
            full_string += json_data[element]+"\n";
          });

        return full_string;
    }

    public Errors(){

    }
}
