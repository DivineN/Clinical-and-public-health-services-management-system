/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Dao.AppointmentDao;
import Dao.ConsultationDao;

import Dao.DoctorsDao;
import Dao.HospitalDao;
import Dao.LocationDao;
import Dao.OperationDao;
import Dao.PatientDao;
import Dao.UserDao;
import Domain.Appointment;

import Domain.Consultation;

import Domain.Doctors;
import Domain.Hospital;
import Domain.Location;
import Domain.Operation;
import Domain.Patients;
import Domain.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Rugwiro
 */
@ManagedBean
@SessionScoped
public class AdminModel {

    private User u = new User();
    private Hospital hospital = new Hospital();
    HospitalDao hospitalDao = new HospitalDao();
    UserDao userDao = new UserDao();
    private String unipassword = new String();
    private List<Doctors> docList = new DoctorsDao().findAll(Doctors.class);
    private List<Patients> patList = new PatientDao().findAll(Patients.class);
    private List<Appointment> appList = new AppointmentDao().findAll(Appointment.class);
    private List<Hospital> hosList = new HospitalDao().findAll(Hospital.class);
    private List<Operation> opList = new OperationDao().findAll(Operation.class);
    private Doctors docdetails = new Doctors();
    private Hospital hosdetails = new Hospital();
    DoctorsDao docDao = new DoctorsDao();
    PatientDao aptDao = new PatientDao();
    private Doctors doctors = new Doctors();
    private Patients patients = new Patients();
    private List<User> userList = new UserDao().findAll(User.class);

    private List<Location> location = new LocationDao().findAll(Location.class);
    private List<Location> prov = new ArrayList<>();
    private List<Location> dis = new ArrayList<>();
    private String ProvId = new String();
    private String DistId = new String();

    String hospitalName;
    String dropHospital;

    @PostConstruct
    public void init() {
        hosList = new HospitalDao().findAll(Hospital.class);
        prov = new ArrayList<>();
        for (Location loc : location) {
            if (loc.getLocationType().equals("PROVINCE")) {
                prov.add(loc);
            }
        }

    }

    public void distrPro() {
        dis = new ArrayList<>();
        for (Location loc : location) {
            if (loc.getLocation() != null && loc.getLocation().getLId().equals(ProvId)) {
                dis.add(loc);
            }
        }
    }

    public Location finalLoc() {
        for (Location loc : location) {
            if (loc.getLId().equals(DistId)) {
                return loc;
            }
        }
        return null;
    }

    public String fetchHospital() {

        hospitalName = dropHospital;

        System.out.println("The hospital is " + hospitalName);
        return hospitalName;

    }

    public void createHospital() {
        hospital.setLocation(finalLoc());
        new HospitalDao().save(hospital);
        u.setPassword(u.getPassword());
        u.setAccess("Hospital");
        u.setHospital(hospital);

        new UserDao().save(u);

        u = new User();
        hospital = new Hospital();

        hosList = new HospitalDao().findAll(Hospital.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Hospital Registered!", null));
    }

    public void createDoctor() {

        new DoctorsDao().save(doctors);
        u.setPassword(u.getPassword());
        u.setAccess("Doctor");
        u.setDoctors(doctors);
        new UserDao().save(u);

        u = new User();
        doctors = new Doctors();

        docList = new DoctorsDao().findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Doctor Registered!", null));
    }

    public void createPatient() {

        new PatientDao().save(patients);
        u.setPassword(u.getPassword());
        u.setAccess("Patient");
        u.setPatients(patients);
        new UserDao().save(u);

        u = new User();
        patients = new Patients();

        patList = new PatientDao().findAll(Patients.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Account Created!", null));
    }

    public void updateDoctor() {

        docDao.update(docdetails);
        docdetails = new Doctors();
        docList = docDao.findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Doctor Updated!", null));
    }

    public String deleteHospital() {
        
        userList.forEach((us) -> {
            if (us.getAccess().matches("Hospital") && hosdetails.getUser().getId().equals(us.getHospital().getUser().getId())) {

             userDao.delete(us);
             us =new User();
                } 

            

        });
        hospitalDao.delete(this.hosdetails);
        hosList = new HospitalDao().findAll(Hospital.class);
        FacesContext ct = FacesContext.getCurrentInstance();
        ct.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted!", ""));
        return "viewHospitals.xhtml?faces-redirect=true";
    }


    public String fetchItems(final Doctors doc) {
        this.docdetails = doc;
        return "EditDoctors.xhtml?faces-redirect=true";
    }

    public void updateHospital() {

        hospitalDao.update(hosdetails);
        hosdetails = new Hospital();
        hosList = hospitalDao.findAll(Hospital.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Hospital Updated!", null));
    }

    public String fetchItems2(final Hospital hos) {
        this.hosdetails = hos;
        return "EditHospital.xhtml?faces-redirect=true";
    }

    public void deleteDoctor() {
        docDao.delete(this.docdetails);
        docList = docDao.findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted!", null));
    }

    public void fetchAndShow(final Doctors doc) {
        this.docdetails = doc;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void fetchAndShow2(final Hospital hos) {
        this.hosdetails = hos;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void clearDoctorDetails() {
        this.docdetails = null;
    }

    public void clearHospitalDetails() {
        this.hosdetails = null;
    }

    public List<Doctors> viewDoctors() {
        List<Doctors> list = new DoctorsDao().findAll(Doctors.class);
        return list;
    }

    public List<Hospital> viewHospitals() {
        List<Hospital> list = new HospitalDao().findAll(Hospital.class);
        return list;
    }

    public List<Consultation> viewConsultations() {
        List<Consultation> list = new ConsultationDao().findAll(Doctors.class);
        return list;
    }

    public void createPDFHosiptals() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            Rectangle rect = new Rectangle(20, 20, 800, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setBoxSize("art", rect);
            document.setPageSize(rect);
            if (!document.isOpen()) {
                document.open();
            }
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Dashboard");
            path = path.substring(0, path.indexOf("\\build"));
            path = path + "\\web\\Dashboard\\logo.jpg";
            Image image = Image.getInstance(path);
            image.scaleAbsolute(50, 50);
            image.setAlignment(Element.ALIGN_LEFT);
            Paragraph title = new Paragraph();
            //BEGIN page
            title.add(image);
            title.add("\n Clinical and Public Health Services Management System");
            title.add("\n P.O. Box 4439 KIGALI - RWANDA");
            title.add("\n Email: info@rbc.gov.rw");
            document.add(title);

            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);

            document.add(new Paragraph("\n"));
            Paragraph para = new Paragraph(" Hospitals List  ", font0);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
            cell1.setBorder(Rectangle.BOX);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Name", font2));
            cell2.setBorder(Rectangle.BOX);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Phone", font2));
            cell3.setBorder(Rectangle.BOX);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Location ", font2));
            cell4.setBorder(Rectangle.BOX);
            table.addCell(cell4);

            PdfPCell pdfc1;
            PdfPCell pdfc2;
            PdfPCell pdfc3;
            PdfPCell pdfc4;
            PdfPCell pdfc5;
//            PdfPCell pdfc6;

            int i = 1;

            for (Hospital hos : hosList = new HospitalDao().findAll(Hospital.class)) {
                pdfc1 = new PdfPCell(new Phrase(hos.getId().toString(), font6));
                pdfc1.setBorder(Rectangle.BOX);
                table.addCell(pdfc1);

                pdfc2 = new PdfPCell(new Phrase(hos.getName(), font6));
                pdfc2.setBorder(Rectangle.BOX);
                table.addCell(pdfc2);

                pdfc3 = new PdfPCell(new Phrase(hos.getPhone(), font6));
                pdfc3.setBorder(Rectangle.BOX);
                table.addCell(pdfc3);

                pdfc4 = new PdfPCell(new Phrase(hos.getLocation().getLocationName(), font6));
                pdfc4.setBorder(Rectangle.BOX);
                table.addCell(pdfc4);

            }
            document.add(table);
            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
            Paragraph ps = new Paragraph("\n Clinical and Public Health Services Management System ", font1);
            ps.setAlignment(Element.ALIGN_RIGHT);
            document.add(ps);
            document.close();
            String fileName = "Hospitals Report";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createPDFDoctors() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            Rectangle rect = new Rectangle(20, 20, 800, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setBoxSize("art", rect);
            document.setPageSize(rect);
            if (!document.isOpen()) {
                document.open();
            }
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Dashboard");
            path = path.substring(0, path.indexOf("\\build"));
            path = path + "\\web\\Dashboard\\logo.jpg";
            Image image = Image.getInstance(path);
            image.scaleAbsolute(50, 50);
            image.setAlignment(Element.ALIGN_LEFT);
            Paragraph title = new Paragraph();
            //BEGIN page
            title.add(image);
            title.add("\n Clinical and Public Health Services Management System");
            title.add("\n P.O. Box 4439 KIGALI - RWANDA");
            title.add("\n Email: info@rbc.gov.rw");
            document.add(title);

            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);

            document.add(new Paragraph("\n"));
            Paragraph para = new Paragraph(" Doctors List  ", font0);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
            cell1.setBorder(Rectangle.BOX);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Name", font2));
            cell2.setBorder(Rectangle.BOX);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Phone", font2));
            cell3.setBorder(Rectangle.BOX);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Email ", font2));
            cell4.setBorder(Rectangle.BOX);
            table.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase("Hospital ", font2));
            cell5.setBorder(Rectangle.BOX);
            table.addCell(cell5);

            PdfPCell pdfc1;
            PdfPCell pdfc2;
            PdfPCell pdfc3;
            PdfPCell pdfc4;
            PdfPCell pdfc5;
//            PdfPCell pdfc6;

            int i = 1;

            for (Doctors doc : docList = new DoctorsDao().findAll(Doctors.class)) {
                pdfc1 = new PdfPCell(new Phrase(doc.getId().toString(), font6));
                pdfc1.setBorder(Rectangle.BOX);
                table.addCell(pdfc1);

                pdfc2 = new PdfPCell(new Phrase(doc.getFName() + " " + doc.getLName(), font6));
                pdfc2.setBorder(Rectangle.BOX);
                table.addCell(pdfc2);

                pdfc3 = new PdfPCell(new Phrase(doc.getPhone(), font6));
                pdfc3.setBorder(Rectangle.BOX);
                table.addCell(pdfc3);

                pdfc4 = new PdfPCell(new Phrase(doc.getEmail(), font6));
                pdfc4.setBorder(Rectangle.BOX);
                table.addCell(pdfc4);

                pdfc5 = new PdfPCell(new Phrase(doc.getHospital().getName(), font6));
                pdfc5.setBorder(Rectangle.BOX);
                table.addCell(pdfc5);

            }
            document.add(table);
            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
            Paragraph ps = new Paragraph("\n Clinical and Public Health Services Management System ", font1);
            ps.setAlignment(Element.ALIGN_RIGHT);
            document.add(ps);
            document.close();
            String fileName = "Doctors Report";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void createPDFPatients() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            Rectangle rect = new Rectangle(20, 20, 800, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setBoxSize("art", rect);
            document.setPageSize(rect);
            if (!document.isOpen()) {
                document.open();
            }
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Dashboard");
            path = path.substring(0, path.indexOf("\\build"));
            path = path + "\\web\\Dashboard\\logo.jpg";
            Image image = Image.getInstance(path);
            image.scaleAbsolute(50, 50);
            image.setAlignment(Element.ALIGN_LEFT);
            Paragraph title = new Paragraph();
            //BEGIN page
            title.add(image);
            title.add("\n Clinical and Public Health Services Management System");
            title.add("\n P.O. Box 4439 KIGALI - RWANDA");
            title.add("\n Email: info@rbc.gov.rw");
            document.add(title);

            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);

            document.add(new Paragraph("\n"));
            Paragraph para = new Paragraph(" Patients List  ", font0);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
            cell1.setBorder(Rectangle.BOX);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Name", font2));
            cell2.setBorder(Rectangle.BOX);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Phone", font2));
            cell3.setBorder(Rectangle.BOX);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Insurance ", font2));
            cell4.setBorder(Rectangle.BOX);
            table.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase("Location ", font2));
            cell5.setBorder(Rectangle.BOX);
            table.addCell(cell5);

            PdfPCell pdfc1;
            PdfPCell pdfc2;
            PdfPCell pdfc3;
            PdfPCell pdfc4;
            PdfPCell pdfc5;
//            PdfPCell pdfc6;

            int i = 1;

            for (Patients pat : patList = new PatientDao().findAll(Patients.class)) {
                pdfc1 = new PdfPCell(new Phrase(pat.getId().toString(), font6));
                pdfc1.setBorder(Rectangle.BOX);
                table.addCell(pdfc1);

                pdfc2 = new PdfPCell(new Phrase(pat.getFName() + " " + pat.getLName(), font6));
                pdfc2.setBorder(Rectangle.BOX);
                table.addCell(pdfc2);

                pdfc3 = new PdfPCell(new Phrase(pat.getPhone(), font6));
                pdfc3.setBorder(Rectangle.BOX);
                table.addCell(pdfc3);

                pdfc4 = new PdfPCell(new Phrase(pat.getInsurance(), font6));
                pdfc4.setBorder(Rectangle.BOX);
                table.addCell(pdfc4);

                pdfc5 = new PdfPCell(new Phrase(pat.getLocation(), font6));
                pdfc5.setBorder(Rectangle.BOX);
                table.addCell(pdfc5);

            }
            document.add(table);
            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
            Paragraph ps = new Paragraph("\n Clinical and Public Health Services Management System ", font1);
            ps.setAlignment(Element.ALIGN_RIGHT);
            document.add(ps);
            document.close();
            String fileName = "Patients Report";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void createPDFAppointment() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            Rectangle rect = new Rectangle(20, 20, 800, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setBoxSize("art", rect);
            document.setPageSize(rect);
            if (!document.isOpen()) {
                document.open();
            }
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Dashboard");
            path = path.substring(0, path.indexOf("\\build"));
            path = path + "\\web\\Dashboard\\logo.jpg";
            Image image = Image.getInstance(path);
            image.scaleAbsolute(50, 50);
            image.setAlignment(Element.ALIGN_LEFT);
            Paragraph title = new Paragraph();
            //BEGIN page
            title.add(image);
            title.add("\n Clinical and Public Health Services Management System");
            title.add("\n P.O. Box 4439 KIGALI - RWANDA");
            title.add("\n Email: info@rbc.gov.rw");
            document.add(title);

            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);

            document.add(new Paragraph("\n"));
            Paragraph para = new Paragraph(" Appointments List  ", font0);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
            cell1.setBorder(Rectangle.BOX);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Customer's Name", font2));
            cell2.setBorder(Rectangle.BOX);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Doctor's Name", font2));
            cell3.setBorder(Rectangle.BOX);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Appointment Date ", font2));
            cell4.setBorder(Rectangle.BOX);
            table.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase("Date of Request ", font2));
            cell5.setBorder(Rectangle.BOX);
            table.addCell(cell5);
            
             PdfPCell cell6 = new PdfPCell(new Phrase("Status ", font2));
            cell6.setBorder(Rectangle.BOX);
            table.addCell(cell6);

            PdfPCell pdfc1;
            PdfPCell pdfc2;
            PdfPCell pdfc3;
            PdfPCell pdfc4;
            PdfPCell pdfc5;
            PdfPCell pdfc6;

            int i = 1;

            for (Appointment app : appList = new AppointmentDao().findAll(Appointment.class)) {
                pdfc1 = new PdfPCell(new Phrase(app.getId().toString(), font6));
                pdfc1.setBorder(Rectangle.BOX);
                table.addCell(pdfc1);

                pdfc2 = new PdfPCell(new Phrase(app.getPatients().getFName() + " " + app.getPatients().getLName(), font6));
                pdfc2.setBorder(Rectangle.BOX);
                table.addCell(pdfc2);

                pdfc3 = new PdfPCell(new Phrase(app.getDoctors().getFName()+" "+app.getDoctors().getLName(), font6));
                pdfc3.setBorder(Rectangle.BOX);
                table.addCell(pdfc3);

                pdfc4 = new PdfPCell(new Phrase(app.getApp_date().toString(), font6));
                pdfc4.setBorder(Rectangle.BOX);
                table.addCell(pdfc4);

                pdfc5 = new PdfPCell(new Phrase(app.getDate().toString(), font6));
                pdfc5.setBorder(Rectangle.BOX);
                table.addCell(pdfc5);
                
                pdfc6 = new PdfPCell(new Phrase(app.getStatus(), font6));
                pdfc6.setBorder(Rectangle.BOX);
                table.addCell(pdfc6);

            }
            document.add(table);
            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
            Paragraph ps = new Paragraph("\n Clinical and Public Health Services Management System ", font1);
            ps.setAlignment(Element.ALIGN_RIGHT);
            document.add(ps);
            document.close();
            String fileName = "Appointments Report";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    public void createPDFOperation() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            Rectangle rect = new Rectangle(20, 20, 800, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setBoxSize("art", rect);
            document.setPageSize(rect);
            if (!document.isOpen()) {
                document.open();
            }
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Dashboard");
            path = path.substring(0, path.indexOf("\\build"));
            path = path + "\\web\\Dashboard\\logo.jpg";
            Image image = Image.getInstance(path);
            image.scaleAbsolute(50, 50);
            image.setAlignment(Element.ALIGN_LEFT);
            Paragraph title = new Paragraph();
            //BEGIN page
            title.add(image);
            title.add("\n Clinical and Public Health Services Management System");
            title.add("\n P.O. Box 4439 KIGALI - RWANDA");
            title.add("\n Email: info@rbc.gov.rw");
            document.add(title);

            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);

            document.add(new Paragraph("\n"));
            Paragraph para = new Paragraph(" Operations List  ", font0);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
            cell1.setBorder(Rectangle.BOX);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Patient", font2));
            cell2.setBorder(Rectangle.BOX);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Docotr", font2));
            cell3.setBorder(Rectangle.BOX);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Description ", font2));
            cell4.setBorder(Rectangle.BOX);
            table.addCell(cell4);

            PdfPCell pdfc1;
            PdfPCell pdfc2;
            PdfPCell pdfc3;
            PdfPCell pdfc4;
            PdfPCell pdfc5;
//            PdfPCell pdfc6;

            int i = 1;

            for (Operation op : opList = new OperationDao().findAll(Operation.class)) {
                pdfc1 = new PdfPCell(new Phrase(op.getId().toString(), font6));
                pdfc1.setBorder(Rectangle.BOX);
                table.addCell(pdfc1);

                pdfc2 = new PdfPCell(new Phrase(op.getPatients().getFName()+" "+op.getPatients().getLName(), font6));
                pdfc2.setBorder(Rectangle.BOX);
                table.addCell(pdfc2);

                pdfc3 = new PdfPCell(new Phrase(op.getDoctors().getFName()+" "+op.getDoctors().getLName(), font6));
                pdfc3.setBorder(Rectangle.BOX);
                table.addCell(pdfc3);

                pdfc4 = new PdfPCell(new Phrase(op.getDescription(), font6));
                pdfc4.setBorder(Rectangle.BOX);
                table.addCell(pdfc4);

            }
            document.add(table);
            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
            Paragraph ps = new Paragraph("\n Clinical and Public Health Services Management System ", font1);
            ps.setAlignment(Element.ALIGN_RIGHT);
            document.add(ps);
            document.close();
            String fileName = "Operation Report";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) {
        try {
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Expires", "0");
            externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            externalContext.setResponseHeader("Pragma", "public");
            externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
            externalContext.setResponseContentLength(baos.size());
            OutputStream out = externalContext.getResponseOutputStream();
            baos.writeTo(out);
            externalContext.responseFlushBuffer();
        } catch (IOException e) {

        }
    }

    public List<Doctors> getDocList() {
        return docList;
    }

    public void setDocList(List<Doctors> docList) {
        this.docList = docList;
    }

    public Doctors getDocdetails() {
        return docdetails;
    }

    public void setDocdetails(Doctors docdetails) {
        this.docdetails = docdetails;
    }

    public DoctorsDao getDocDao() {
        return docDao;
    }

    public void setDocDao(DoctorsDao docDao) {
        this.docDao = docDao;
    }

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public String getUnipassword() {
        return unipassword;
    }

    public void setUnipassword(String unipassword) {
        this.unipassword = unipassword;
    }

    public Doctors getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctors doctors) {
        this.doctors = doctors;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public HospitalDao getHospitalDao() {
        return hospitalDao;
    }

    public void setHospitalDao(HospitalDao hospitalDao) {
        this.hospitalDao = hospitalDao;
    }

    public List<Hospital> getHosList() {
        return hosList;
    }

    public void setHosList(List<Hospital> hosList) {
        this.hosList = hosList;
    }

    public Hospital getHosdetails() {
        return hosdetails;
    }

    public void setHosdetails(Hospital hosdetails) {
        this.hosdetails = hosdetails;
    }

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }

    public List<Location> getProv() {
        return prov;
    }

    public void setProv(List<Location> prov) {
        this.prov = prov;
    }

    public List<Location> getDis() {
        return dis;
    }

    public void setDis(List<Location> dis) {
        this.dis = dis;
    }

    public String getProvId() {
        return ProvId;
    }

    public void setProvId(String ProvId) {
        this.ProvId = ProvId;
    }

    public String getDistId() {
        return DistId;
    }

    public void setDistId(String DistId) {
        this.DistId = DistId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDropHospital() {
        return dropHospital;
    }

    public void setDropHospital(String dropHospital) {
        this.dropHospital = dropHospital;
    }

    public List<Patients> getPatList() {
        return patList;
    }

    public void setPatList(List<Patients> patList) {
        this.patList = patList;
    }

    public PatientDao getAptDao() {
        return aptDao;
    }

    public void setAptDao(PatientDao aptDao) {
        this.aptDao = aptDao;
    }

    public Patients getPatients() {
        return patients;
    }

    public void setPatients(Patients patients) {
        this.patients = patients;
    }

    public List<Appointment> getAppList() {
        return appList;
    }

    public void setAppList(List<Appointment> appList) {
        this.appList = appList;
    }

    public List<Operation> getOpList() {
        return opList;
    }

    public void setOpList(List<Operation> opList) {
        this.opList = opList;
    }

}
