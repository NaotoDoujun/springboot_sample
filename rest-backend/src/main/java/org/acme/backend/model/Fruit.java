package org.acme.backend.model;

public class Fruit {
    
	private long id;
    private String name;

    public Fruit() {
    }

    public Fruit(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
