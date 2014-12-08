package com.ndl.android.babymaster.datamodel;

public class RecordModel {

	public int babyId;
	public String type;
	public String registrationDate;
	public int recordInt = -1;
	public String recordStr;
	
	public String toString(){
		Object[] ary = new Object[5];
		ary[0] = this.babyId;
		ary[1] = this.type;
		ary[2] = this.registrationDate;
		ary[3] = this.recordInt;
		ary[4] = this.recordStr;
		return String.format("-babyId:%s\n-type:%s\n-registrationDate:%s\n-recordInt:%s\n-recordStr:%s", ary);
	}
	
	/*
	@Override
	public int compareTo(RecordModel another) {
		if(this.babyId == another.babyId) return 1; 
		return 0;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(babyId);
		dest.writeString(type);
		dest.writeString(registrationDate);
		dest.writeInt(recordInt);
		dest.writeString(recordStr);
	}
	
	public static final Parcelable.Creator<RecordModel> CREATOR = new Parcelable.Creator<RecordModel>() {
		public RecordModel createFromParcel( Parcel source ){
			RecordModel model = new RecordModel();
			model.babyId = source.readInt();
			model.type = source.readString();
			model.registrationDate = source.readString();
			model.recordInt = source.readInt();
			model.recordStr = source.readString();
			return model;
		}
		
		public RecordModel[] newArray( int size )
		{
			return new RecordModel[size];
		}
	};
	 */
}
