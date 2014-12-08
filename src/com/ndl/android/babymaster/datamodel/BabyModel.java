package com.ndl.android.babymaster.datamodel;

public class BabyModel {

	public String id;
	public String nameKor;
	public String nameEng;
	public String picturePath;
	public String birthDay;
	public String sex;
	public String height;
	public String weight;
	
	@Override
	public String toString(){
		Object[] ary = new Object[8];
		ary[0] = this.id;
		ary[1] = this.nameKor;
		ary[2] = this.nameEng;
		ary[3] = this.picturePath;
		ary[4] = this.birthDay;
		ary[5] = this.sex;
		ary[6] = this.height;
		ary[7] = this.weight;
		return String.format("-id:%s\n-nameKor:%s\n-nameEng:%s\n-picturePath:%s\n-birthDay:%s\n-sex:%s\n-height:%s\n-weight:%s", ary);
	}
	
	/*
	@Override
	public int compareTo(BabyModel another) {
		if(this.id.equals(another.id)) return 1; 
		return 0;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(nameKor);
		dest.writeString(nameEng);
		dest.writeString(picturePath);
		dest.writeString(birthDay);
		dest.writeString(sex);
		dest.writeString(height);
		dest.writeString(weight);
	}
	
	public static final Parcelable.Creator<BabyModel> CREATOR = new Parcelable.Creator<BabyModel>() {
		public BabyModel createFromParcel( Parcel source ){
			BabyModel model = new BabyModel();
			model.id = source.readString();
			model.nameKor = source.readString();
			model.nameEng = source.readString();
			model.picturePath = source.readString();
			model.birthDay = source.readString();
			model.sex = source.readString();
			model.height = source.readString();
			model.weight = source.readString();
			return model;
		}
		
		public BabyModel[] newArray( int size )
		{
			return new BabyModel[size];
		}
	};
	*/
}
